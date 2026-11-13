package com.archer.tools.test.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.archer.tools.java.ArcherList;
import com.archer.tools.java.ArcherMap;

public class ContainerTest {

    static int failed = 0;

    static void check(boolean cond, String msg) {
        if(!cond) {
            failed++;
            System.out.println("[FAIL] " + msg);
        }
    }

    /** 随机操作序列下与 ArrayList 对照 */
    static void testListAgainstOracle() {
        Random rnd = new Random(42);
        List<String> oracle = new ArrayList<>();
        List<String> mine = new ArcherList<>();
        for(int op = 0; op < 200000; op++) {
            int kind = rnd.nextInt(10);
            switch(kind) {
            case 0: case 1: case 2: case 3: {
                String v = rnd.nextInt(50) == 0 ? null : "v" + rnd.nextInt(2000);
                oracle.add(v);
                mine.add(v);
                break;
            }
            case 4: {
                String v = "v" + rnd.nextInt(2000);
                boolean a = oracle.remove(v);
                boolean b = mine.remove(v);
                check(a == b, "list remove(Object) ret mismatch op=" + op);
                break;
            }
            case 5: {
                if(!oracle.isEmpty()) {
                    int idx = rnd.nextInt(oracle.size());
                    check(eq(oracle.get(idx), mine.get(idx)), "list get mismatch op=" + op);
                }
                break;
            }
            case 6: {
                if(!oracle.isEmpty()) {
                    int idx = rnd.nextInt(oracle.size());
                    String v = "r" + rnd.nextInt(100);
                    oracle.set(idx, v);
                    mine.set(idx, v);
                }
                break;
            }
            case 7: {
                String v = "v" + rnd.nextInt(2000);
                check(oracle.contains(v) == mine.contains(v), "list contains mismatch op=" + op);
                break;
            }
            case 8: {
                if(!oracle.isEmpty()) {
                    int idx = rnd.nextInt(oracle.size());
                    check(eq(oracle.remove(idx), mine.remove(idx)), "list remove(int) mismatch op=" + op);
                }
                break;
            }
            case 9: {
                if(rnd.nextInt(20) == 0) {
                    oracle.clear();
                    mine.clear();
                }
                break;
            }
            }
            if(op % 5000 == 0) {
                check(oracle.size() == mine.size(), "list size mismatch op=" + op);
            }
        }
        check(oracle.size() == mine.size(), "list final size");
        check(oracle.equals(new ArrayList<>(mine)), "list final content equals");
        Object[] arr = mine.toArray();
        check(arr.length == mine.size(), "toArray length == size");
        String[] typed = mine.toArray(new String[0]);
        check(typed.length == mine.size(), "toArray(T[]) length == size");
        System.out.println("[PASS] ArcherList vs ArrayList oracle, final size=" + mine.size());
    }

    static void testListEdgeCases() {
        // 删除最后一个元素
        List<String> l = new ArcherList<>();
        l.add("a");
        l.add("b");
        check(l.remove("b"), "remove last returns true");
        check(l.size() == 1, "remove last shrinks size");
        check(!l.contains("b"), "removed last no longer contained");
        check(!l.remove("zzz"), "remove absent returns false");

        // containsAll 方向
        List<String> a = new ArcherList<>();
        a.add("x");
        a.add("y");
        List<String> b = new ArcherList<>();
        b.add("x");
        check(a.containsAll(b), "containsAll superset true");
        check(!b.containsAll(a), "containsAll subset false");

        // null 处理
        List<String> n = new ArcherList<>();
        n.add(null);
        n.add("s");
        check(n.contains(null), "contains null");
        n.remove(null);
        check(!n.contains(null), "null removed");

        // add(int, null) 更新 hasNull
        List<String> m = new ArcherList<>();
        m.add("p");
        m.add(0, null);
        check(m.contains(null), "add(int,null) tracked");

        // retainAll / removeAll
        List<String> r = new ArcherList<>();
        for(int i = 0; i < 10; i++) {
            r.add("k" + i);
        }
        List<String> keep = new ArrayList<>();
        keep.add("k1");
        keep.add("k3");
        r.retainAll(keep);
        check(r.size() == 2 && r.contains("k1") && r.contains("k3"), "retainAll");

        // subList + addAll(index)
        List<String> s = new ArcherList<>();
        for(int i = 0; i < 5; i++) {
            s.add("s" + i);
        }
        List<String> sub = s.subList(1, 3);
        check(sub.size() == 2 && "s1".equals(sub.get(0)), "subList");
        s.addAll(2, keep);
        check(s.size() == 7 && "k1".equals(s.get(2)) && "s2".equals(s.get(4)), "addAll(index)");

        System.out.println("[PASS] ArcherList edge cases");
    }

    /** 随机操作序列下与 HashMap 对照（覆盖 <32 线性路径和 >=32 hash 路径） */
    static void testMapAgainstOracle() {
        Random rnd = new Random(7);
        Map<String, Integer> oracle = new HashMap<>();
        ArcherMap<String, Integer> mine = new ArcherMap<>();
        for(int op = 0; op < 300000; op++) {
            int kind = rnd.nextInt(10);
            String key = "k" + rnd.nextInt(3000);
            switch(kind) {
            case 0: case 1: case 2: case 3: case 4: {
                Integer a = oracle.put(key, op);
                Integer b = mine.put(key, op);
                check(eq(a, b), "map put ret mismatch op=" + op);
                break;
            }
            case 5: case 6: {
                Integer a = oracle.remove(key);
                Integer b = mine.remove(key);
                check(eq(a, b), "map remove ret mismatch op=" + op);
                check(oracle.size() == mine.size(), "map size after remove op=" + op);
                break;
            }
            case 7: case 8: {
                check(eq(oracle.get(key), mine.get(key)), "map get mismatch op=" + op);
                break;
            }
            case 9: {
                check(oracle.containsKey(key) == mine.containsKey(key), "map containsKey mismatch op=" + op);
                if(rnd.nextInt(50) == 0) {
                    oracle.clear();
                    mine.clear();
                }
                break;
            }
            }
        }
        check(oracle.size() == mine.size(), "map final size");
        for(Map.Entry<String, Integer> e : oracle.entrySet()) {
            check(eq(e.getValue(), mine.get(e.getKey())), "map final content key=" + e.getKey());
        }
        System.out.println("[PASS] ArcherMap vs HashMap oracle, final size=" + mine.size());
    }

    static void testMapEdgeCases() {
        // 插入顺序 + 下标访问
        ArcherMap<String, Integer> m = new ArcherMap<>();
        for(int i = 0; i < 100; i++) {
            m.put("key" + i, i);
        }
        boolean ordered = true;
        for(int i = 0; i < 100; i++) {
            if(!("key" + i).equals(m.getKeyByIndex(i)) || m.getValueByIndex(i) != i) {
                ordered = false;
                break;
            }
        }
        check(ordered, "insertion order by index (>=32, hash path)");

        // 删除后下标连续且顺序保持
        for(int i = 0; i < 50; i++) {
            m.remove("key" + i);
        }
        check(m.size() == 50, "size after 50 removes");
        ordered = true;
        for(int i = 0; i < 50; i++) {
            if(!("key" + (i + 50)).equals(m.getKeyByIndex(i))) {
                ordered = false;
                break;
            }
        }
        check(ordered, "order preserved after removes");

        // entrySet 迭代内容
        int sum = 0, cnt = 0;
        for(Map.Entry<String, Integer> e : m.entrySet()) {
            sum += e.getValue();
            cnt++;
        }
        check(cnt == 50 && sum == 3725, "entrySet iteration content");

        // keySet/values 视图实时反映
        m.put("extra", 1);
        check(m.keySet().size() == 51 && m.values().size() == 51, "views reflect live size");
        check(m.keySet().contains("extra") && m.values().contains(1), "view contains");

        // putAll 大批量
        ArcherMap<String, Integer> big = new ArcherMap<>();
        Map<String, Integer> src = new HashMap<>();
        for(int i = 0; i < 5000; i++) {
            src.put("b" + i, i);
        }
        big.putAll(src);
        check(big.size() == 5000, "putAll size");
        check(big.get("b4999") == 4999 && big.get("b0") == 0, "putAll content (hash path)");

        // toString 无尾逗号
        ArcherMap<String, String> t = ArcherMap.of("a", "1", "b", "2");
        check("{\"a\":\"1\", \"b\":\"2\"}".equals(t.toString()), "toString format: " + t);

        // 更新已存在 key
        ArcherMap<String, Integer> u = new ArcherMap<>();
        for(int i = 0; i < 40; i++) {
            u.put("u" + i, i);
        }
        check(u.put("u20", 999) == 20, "put existing returns old");
        check(u.get("u20") == 999 && u.size() == 40, "put existing updates value");

        System.out.println("[PASS] ArcherMap edge cases");
    }

    static boolean eq(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    static void perf() {
        // 小 map（8 元素，目标场景）get 对比
        ArcherMap<String, Integer> am = new ArcherMap<>();
        HashMap<String, Integer> hm = new HashMap<>();
        for(int i = 0; i < 8; i++) {
            am.put("k" + i, i);
            hm.put("k" + i, i);
        }
        int loops = 3000000;
        long s = 0;
        // 预建查找键（与 map 中 key 内容相同的不同实例），排除循环内字符串拼接的分配干扰
        String[] lookupKeys = new String[8];
        for(int i = 0; i < 8; i++) {
            lookupKeys[i] = new String("k" + i);
        }
        // JIT 预热，消除测量顺序偏差
        for(int i = 0; i < 1000000; i++) {
            s += am.get(lookupKeys[i & 7]);
            s += hm.get(lookupKeys[i & 7]);
        }
        // ABAB 交替测 5 轮取最优，排除 JIT/GC 抖动
        long tAmSmall = Long.MAX_VALUE, tHmSmall = Long.MAX_VALUE;
        for(int r = 0; r < 5; r++) {
            long t1 = System.nanoTime();
            for(int i = 0; i < loops; i++) {
                s += am.get(lookupKeys[i & 7]);
            }
            tAmSmall = Math.min(tAmSmall, System.nanoTime() - t1);
            t1 = System.nanoTime();
            for(int i = 0; i < loops; i++) {
                s += hm.get(lookupKeys[i & 7]);
            }
            tHmSmall = Math.min(tHmSmall, System.nanoTime() - t1);
        }
        System.out.println(String.format("[PERF] small map(8) get x%d (best of 5): ArcherMap=%dms HashMap=%dms",
                loops, tAmSmall / 1000000, tHmSmall / 1000000));

        // 相同实例 key（如常量/interned 字符串）场景：== 快路径
        String[] sameKeys = new String[8];
        for(int i = 0; i < 8; i++) {
            sameKeys[i] = "k" + i; // 与 map 中 key 内容相同的不同实例，改为直接取 map 中的实例
        }
        int idx = 0;
        for(String k : am.keySet()) {
            sameKeys[idx++] = k; // 直接复用 map 内部 key 实例，走 == 路径
        }
        long tAmSame = Long.MAX_VALUE, tHmSame = Long.MAX_VALUE;
        for(int i = 0; i < 1000000; i++) {
            s += am.get(sameKeys[i & 7]);
            s += hm.get(sameKeys[i & 7]);
        }
        for(int r = 0; r < 5; r++) {
            long t1 = System.nanoTime();
            for(int i = 0; i < loops; i++) {
                s += am.get(sameKeys[i & 7]);
            }
            tAmSame = Math.min(tAmSame, System.nanoTime() - t1);
            t1 = System.nanoTime();
            for(int i = 0; i < loops; i++) {
                s += hm.get(sameKeys[i & 7]);
            }
            tHmSame = Math.min(tHmSame, System.nanoTime() - t1);
        }
        System.out.println(String.format("[PERF] small map(8) get same-instance keys x%d (best of 5): ArcherMap=%dms HashMap=%dms",
                loops, tAmSame / 1000000, tHmSame / 1000000));

        // 大 map（5000 元素，超出目标场景）get 对比
        ArcherMap<String, Integer> amBig = new ArcherMap<>();
        HashMap<String, Integer> hmBig = new HashMap<>();
        for(int i = 0; i < 5000; i++) {
            amBig.put("k" + i, i);
            hmBig.put("k" + i, i);
        }
        long tAmBig = Long.MAX_VALUE, tHmBig = Long.MAX_VALUE;
        for(int i = 0; i < 1000000; i++) {
            s += amBig.get("k" + (i % 5000));
            s += hmBig.get("k" + (i % 5000));
        }
        for(int r = 0; r < 5; r++) {
            long t1 = System.nanoTime();
            for(int i = 0; i < loops; i++) {
                s += amBig.get("k" + (i % 5000));
            }
            tAmBig = Math.min(tAmBig, System.nanoTime() - t1);
            t1 = System.nanoTime();
            for(int i = 0; i < loops; i++) {
                s += hmBig.get("k" + (i % 5000));
            }
            tHmBig = Math.min(tHmBig, System.nanoTime() - t1);
        }
        System.out.println(String.format("[PERF] big map(5000) get x%d (best of 5): ArcherMap=%dms HashMap=%dms",
                loops, tAmBig / 1000000, tHmBig / 1000000));

        // list add + 遍历 对比
        int n = 1000000;
        List<Integer> al = new ArcherList<>();
        long t1 = System.nanoTime();
        for(int i = 0; i < n; i++) {
            al.add(i);
        }
        long tAlAdd = System.nanoTime() - t1;
        List<Integer> jl = new ArrayList<>();
        t1 = System.nanoTime();
        for(int i = 0; i < n; i++) {
            jl.add(i);
        }
        long tJlAdd = System.nanoTime() - t1;
        System.out.println(String.format("[PERF] list add x%d: ArcherList=%dms ArrayList=%dms",
                n, tAlAdd / 1000000, tJlAdd / 1000000));
        // 小 map 批量构建（put 全新 key，HashMap 每插一个都要分配 Node）
        int builds = 300000;
        long tAmPut = Long.MAX_VALUE, tHmPut = Long.MAX_VALUE;
        for(int r = 0; r < 5; r++) {
            long t2 = System.nanoTime();
            for(int b = 0; b < builds; b++) {
                ArcherMap<String, Integer> m = new ArcherMap<>();
                for(int i = 0; i < 8; i++) {
                    m.put(lookupKeys[i], i);
                }
                s += m.size();
            }
            tAmPut = Math.min(tAmPut, System.nanoTime() - t2);
            t2 = System.nanoTime();
            for(int b = 0; b < builds; b++) {
                HashMap<String, Integer> m = new HashMap<>();
                for(int i = 0; i < 8; i++) {
                    m.put(lookupKeys[i], i);
                }
                s += m.size();
            }
            tHmPut = Math.min(tHmPut, System.nanoTime() - t2);
        }
        System.out.println(String.format("[PERF] small map(8) build x%d (best of 5): ArcherMap=%dms HashMap=%dms",
                builds, tAmPut / 1000000, tHmPut / 1000000));

        // 小 map entrySet 全量迭代
        long tAmIt = Long.MAX_VALUE, tHmIt = Long.MAX_VALUE;
        for(int i = 0; i < 300000; i++) {
            for(Map.Entry<String, Integer> e : am.entrySet()) {
                s += e.getValue();
            }
            for(Map.Entry<String, Integer> e : hm.entrySet()) {
                s += e.getValue();
            }
        }
        for(int r = 0; r < 5; r++) {
            long t3 = System.nanoTime();
            for(int i = 0; i < loops / 8; i++) {
                for(Map.Entry<String, Integer> e : am.entrySet()) {
                    s += e.getValue();
                }
            }
            tAmIt = Math.min(tAmIt, System.nanoTime() - t3);
            t3 = System.nanoTime();
            for(int i = 0; i < loops / 8; i++) {
                for(Map.Entry<String, Integer> e : hm.entrySet()) {
                    s += e.getValue();
                }
            }
            tHmIt = Math.min(tHmIt, System.nanoTime() - t3);
        }
        System.out.println(String.format("[PERF] small map(8) entrySet iterate x%d entries (best of 5): ArcherMap=%dms HashMap=%dms",
                loops, tAmIt / 1000000, tHmIt / 1000000));

        System.out.println("[PERF] checksum=" + s);
    }

    public static void main(String[] args) {
        if(args.length > 0 && "perf".equals(args[0])) {
            perf();
            return;
        }
        testListAgainstOracle();
        testListEdgeCases();
        testMapAgainstOracle();
        testMapEdgeCases();
        if(failed > 0) {
            System.out.println("FAILED: " + failed + " checks");
            System.exit(1);
        }
        perf();
        System.out.println("ALL PASS");
    }
}
