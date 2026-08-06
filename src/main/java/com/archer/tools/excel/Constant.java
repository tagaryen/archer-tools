package com.archer.tools.excel;

final class Constant {

    final static String sheetFormat = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" xmlns:x14ac=\"http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac\" mc:Ignorable=\"x14ac\">\n" +
            "  <dimension ref=\"$(scale)\"/>\n" +
            "  <sheetViews>\n" +
            "    <sheetView tabSelected=\"1\" workbookViewId=\"0\"/>\n" +
            "  </sheetViews>\n" +
            "  <sheetFormatPr defaultRowHeight=\"15\" x14ac:dyDescent=\"0.25\"/>\n" +
            "  $(sheetData)\n" +
            "  $(phonetic)\n" +
            "  <pageMargins left=\"0.7\" right=\"0.7\" top=\"0.75\" bottom=\"0.75\" header=\"0.3\" footer=\"0.3\"/>\n" +
            "</worksheet>";

    final static String scale = "$(scale)";
    final static String sheetData = "$(sheetData)";
    final static String phonetic = "$(phonetic)";


    final static String sharedStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<sst xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" count=\"$(count)\" uniqueCount=\"$(uniqueCount)\">$(strings)</sst>";
    final static String strings = "$(strings)";
    final static String count = "$(count)";
    final static String uniqueCount = "$(uniqueCount)";

    final static String basicData = "UEsDBBQAAAAAAPOYBl0AAAAAAAAAAAAAAAAJAAAAZG9jUHJvcHMvUEsDBBQAAAAIAPGYBl1LBMqlMAEAAFkCAAARAAAAZG9jUHJvcHMvY29yZS54bWyVks1OwzAQhO9IvEPke+KkpVVrJakEqCcqIVEE4mbZ29Qi/pFtSPP2OGmbFpELR+/Mfju7cr46yDr6BuuEVgXKkhRFoJjmQlUFet2u4wWKnKeK01orKFALDq3K25ucGcK0hWerDVgvwEWB" +
            "pBxhpkB77w3B2LE9SOqS4FBB3GkrqQ9PW2FD2SetAE/SdI4leMqpp7gDxmYgohOSswFpvmzdAzjDUIME5R3OkgxfvB6sdKMNvXLllMK3BkatZ3FwH5wYjE3TJM20s3b5M/y+eXrpV42F6m7FAJU5Z4RZoF7b0tOK2hZUjq+K3QFr6vwm3HongN+3ZY5HaqeFjn3AoxCEHGOflbfpw+N2jcpJms3idB6ns222INmSTO8+wsjf/Reg7If8k7gciBdA" +
            "yP3nM5Q/UEsDBBQAAAAAAAWZBl0AAAAAAAAAAAAAAAADAAAAeGwvUEsDBBQAAAAIAAAAIQDNRC/2qgIAAJMHAAANAAAAeGwvc3R5bGVzLnhtbK1VS27bMBDd5xSE9oo+sVTbsBPUcQQESIsCcYFuGYmyifAjkFQqt+gBepheoF3kOFn2CiUtUZItBwiMejU/vjePI47//nmeXVWUgCckJOZs7gTnvgMQS3mG2XrufF4l7tgBUkGWQcIZmjtbJJ2r" +
            "y7OZVFuC7jcIKaARmJw7G6WKqefJdIMolOe8QExnci4oVNoVa08WAsFMmkOUeKHvxx6FmDk1wpSmbwGhUDyWhZtyWkCFHzDBarvDcgBNp7drxgV8ILrTKhjBFFRBLELLYEJDEopTwSXP1bkG9Xie4xQNe514Ew+mFmkHexpSEHl+WAu/PAP6N8s5UxKkvGRKj0BTmDanj4x/ZYlJ6WBT2pbXbhuS38ATJLoucLyDVMoJF0DpBvWdDNMMUlSfffn1" +
            "8+X386AghxSTbV0SDrJGuQWgWN99v2Lm1a12trSKMSGt4rAR12YOOPScFRIs0RnQ2KttodUw/UXu85nTJ4KtBdwGYXQMr7Nt/w9cZEj0ZlbHe7kDWoJydXh3Aq83g6DihQ11gEpxehjNMFxzBslevx1359meU0TIvXm1X/LjjVc5YCVNqLrN5o7eA3pg1jTaG7MGrR3PMvWw+3T/jwlU+QFljT8QNyDcTwMGzTv4aBYKMaiWq8REYfaaLEuTVZ2i" +
            "rk6ZhdNn30FmKIclUSub1LSd/QFluKRhW/UJP3HVVHX2nflCgrglQpW6k82DsgFQCjx3vt8s3k2WN0nojv3F2B1doMidRIulG42uF8tlMvFD//pHbw+etrvMFuyvrrYRjTiVRB8R9T1YXfe7WCOscTpl+48NVcelTcLYfx8Fvptc+IE7iuHYHccXkZtEQbiMR4ubKIl60qLTpAW+FwSvaIumClNEMEP76lZNtNFn3Ldq1GYzzJnX/Zde/gNQSwME" +
            "FAAAAAAAT1oGXQAAAAAAAAAAAAAAAAkAAAB4bC90aGVtZS9QSwMEFAAAAAgAAAAhADqA3ZebBgAAYy4AABMAAAB4bC90aGVtZS90aGVtZTEueG1s7VrJrtMwFN3zFVH2pUmbdEAU1KYt8yBeAbF0U7cxOHEVu0CFkNiyQUICxAaJHQuEhAQrWPA3IAaJX+AmoU3SJNQt5YmpT3pqbZ/j6+Ob62vHX9++O3z0pkuV69jnhHktVT+oqQr2bDYi3qSl" +
            "Xhz0Sw1V4QJ5I0SZh1vqHHP16JEDh9Eh4WAXKwD3+CHUUh0hpofKZW5DMeIH2RR7UDdmvosE/PQn5ZGPbgCtS8sVTauVXUQ8VfGQC6znxmNiY2UQUKpHDijwWfTQo/DPEzwq/V5jU3/PDmqT+AUy0W50TY/KVsr5nFvUV64j2lLBphG7McA3hapQxAVUtFQt/KjlFcZySJnphgrZbhJd9MNPtouILjuSSn4X/mS47MMwTKPWzre6kmO1BGWv3qv1" +
            "avlWZimRbcNc6etpzU6z0zWztEuKfGopi7v1blUvpC60urqeum0Gf8XU1QJqYz11v2/lOtySooDaXE9tGPWKZRRTmwXUtfXUda3dNerF1LUMtUOJd209sWbWqlbOJC4JVsBjRo/LMTdNo1+vZJlTHHFpHGrC4rg/T0hFIBddZX4fWudaRpEgniLmUzxGNrBYiJKhT5TTZOJAOJoij3Eo1ipaX6vC/+DPCL9F85llxChBV9DG5uvbBANUuO2TqWip" +
            "J8EQNYG5MlOOMeEQOzJUhuI48iZJis/P7n95ckf59Orp5wcPJQl4kuDDi7sf3rzbyACRxL9/9PLD65fvH9/7+PyBDL7to2ESPyAu5spZfEO5wFzkyVAcx0P/JykGDiIpCuQAVAbZE04KeXaOqBSwg9Mzd8mHlUwKeWx2NTXePcefCSKDPOW4KeQZxmiH+XIanQLzUhrNvImkvf4sCbyA0HUpc60V3+zNphAaiFSnloNTQz1PwVHRBHtYKEEdu4ax" +
            "DM8VQkhKMmL7jLOxUK4QpYOInPADMhT5LMeJCw4zlxoTeClKsVxSOoxKGdDF19NQBJNHZZADTFOzdwzNBHKlOh0glyahp5FwpAa6N/ft1MRz4YPBmDKlN8KcS5Gc8+epIZ+CRUDSY8/QuZuG+oJck4KeRowloV12zXKQO5UC7xHPSYJP8GvwhCLlPBNS+DMsGVOi3+AgyJP31EsEi58Mphdh7ch39qBm5stwHMMsHePmdIywl80vEqlANkkg3uZJ" +
            "wkp6YP6G6YEM+BclBpLInaYEbZ/IhaogEdgK+I8u/100885jz5HC/l/9/6/+/1f/f2z1l46g+73mxyt7XBzR5Z0quHKHCmNC6Z6YU3ya56cMHOQc9aHVojbbIuxneSoydeBr7tjLabYs08RHYaXiM3GZCGfPQVOwXV+anUXwpd2FLZQp45DZJEg2GciPUHFeNXPPsFEE0/XovFcKyJGIgZopD4Q8TkSwWl0CdbicGN4P9CoHgknpGdi6f5puIk1a" +
            "06o8MNa0LoPavaa69oeI2pQHxqI2dAnUDkSFukVcKFIBNkGwuoITG5HiEI8RxSOIEvmBKwxNf2Hc2vpRSftRRR4Yu0PTkEDtwB32O26BptutBWlNNwByB43wJrjdq7q/kau5QfxJTUdFRp2sqvWGBGwHou5j5IKaldxvtScvXZWpVm5A7lE1wQQbTVvqGM6W4Ks7BVt4kJQjOoFrALZYzO/mueWG+aVEjvmdcepz0UXciQjD9oWEQeItsK9Q4sIS" +
            "lnq8Mq+xvR9IpVfq2n+tJLVqav/9Kq1VtqzwycXjMbZFurq4SVRf2AYYcq1KcfwZffzQGdgMJmPPGd1QhnTmX0DgbWZdD7xwRLgIXTL64S9jb9IVU5mfXNKWuqMgs3wublTQqYO+b/1kMrbD5aiztavSUoLCRyDWcsPpiisLvXY46f9ORxFb9nXkgFzuu9h5ySYu9UV82CLzWD/YP3b/klBU5lHIU9bcLiVsyqSEW2+WdpBL7ve2J5iKzaVJTUV1" +
            "y6nYAAZTseGGd/dT8ev3SqE4GywRW2+VdiDOPu95MstMXBefmUNxWJS5N7xIFoZXYSnrwpn9jApeXpbjm/DWxvp+yy/gT1Ut+4uLlJlPWuotzWwbVsW0SlrD7JWMqqGVGma7WmqbZlXvmbrW7VRur57dC8fVzcjAPryeovPvF6jD8swlanfxcuKgzdwyC98GlENweIlarxRfolbICGysVfrNarNTKzWr7X7J6HYapaZV65S6Nave7Xcts9Hs31aV" +
            "62Fjo121jFqvUarpllUyahqMBRqU6kal0jbq7UbPaN9Wy0n5Qz0Wwsd6LSfhyDdQSwMEFAAAAAgAAAAhAKRTxc8/AQAACAQAABMAAABbQ29udGVudF9UeXBlc10ueG1srZPLbsIwEEX3lfoPlrdVbOiiqioCiz6WLQv6Aa49IRZ+yWMo/H2HhFYqQlBEN7GSuXfOdTweTdbesRVktDHUfCgGnEHQ0dgwr/n77KW65wyLCka5GKDmG0A+GV9fjWab" +
            "BMjIHbDmbSnpQUrULXiFIiYIVGli9qrQa57LpPRCzUHeDgZ3UsdQIJSqbHvw8egJGrV0hT2v6XOfJINDzh57IbEIrVJyVqtCdbkKZo9S7QiCnJ0GW5vwhgRcHiRQ5Qhg53ujX5OtATZVubwqTyq5dvIz5sVHjAtBsvNSxqaxGkzUS08WgSmDMtgCFO9EtwqvbLg5ze/EKLtl+M9BfvqfyFHovKF/Xh6ha3MCiGXjAC9G/d5t3/QYmYzTHBPS5GY4" +
            "n/49mlt3lTJVc7F/JFLr84F724Xt1BswB9iyu8fjL1BLAwQUAAAAAABPWgZdAAAAAAAAAAAAAAAABgAAAF9yZWxzL1BLAwQUAAAACAAAACEAtVUwI+sAAABMAgAACwAAAF9yZWxzLy5yZWxzjZLNSgQxDIDvgu9Qct/J7Aoisp29iLA3kfUBYpv5YWaa0ladfXuLIDqwP3Nsk3z5ErLdTeOgPjnETpyGdVGCYmfEdq7R8HZ4Xj2AiomcpUEcazhy" +
            "hF11e7N95YFSLopt56PKFBc1tCn5R8RoWh4pFuLZ5UgtYaSUn6FBT6anhnFTlvcY/jOgmjHV3moIe3sH6nD0vIQtdd0ZfhLzMbJLJ1ogT4mdZbvyIdeH1HHMeAoNJw1WzEv+jkjeFxkNeNpos9zo/LQ4ciJLidBI4Is+PxmXhNbLha6vaJ7xZzMN+CWhfxfpf11wdgPVN1BLAQI/ABQAAAAAAPOYBl0AAAAAAAAAAAAAAAAJACQAAAAAAAAAEAAA" +
            "AAAAAABkb2NQcm9wcy8KACAAAAAAAAEAGACCGQzKkyXdAQAAAAAAAAAAAAAAAAAAAABQSwECPwAUAAAACADxmAZdSwTKpTABAABZAgAAEQAkAAAAAAAAACAAAAAnAAAAZG9jUHJvcHMvY29yZS54bWwKACAAAAAAAAEAGADu/4zHkyXdAQAAAAAAAAAAAAAAAAAAAABQSwECPwAUAAAAAAAFmQZdAAAAAAAAAAAAAAAAAwAkAAAAAAAAABAAAACG" +
            "AQAAeGwvCgAgAAAAAAABABgAQAd13JMl3QEAAAAAAAAAAAAAAAAAAAAAUEsBAj8AFAAAAAgAAAAhAM1EL/aqAgAAkwcAAA0AJAAAAAAAAAAgAAAApwEAAHhsL3N0eWxlcy54bWwKACAAAAAAAAEAGAAAQLLTXOeoAQAAAAAAAAAAAAAAAAAAAABQSwECPwAUAAAAAABPWgZdAAAAAAAAAAAAAAAACQAkAAAAAAAAABAAAAB8BAAAeGwvdGhlbWUv" +
            "CgAgAAAAAAABABgACtupQFIl3QEAAAAAAAAAAAAAAAAAAAAAUEsBAj8AFAAAAAgAAAAhADqA3ZebBgAAYy4AABMAJAAAAAAAAAAgAAAAowQAAHhsL3RoZW1lL3RoZW1lMS54bWwKACAAAAAAAAEAGAAAQLLTXOeoAQAAAAAAAAAAAAAAAAAAAABQSwECPwAUAAAACAAAACEApFPFzz8BAAAIBAAAEwAkAAAAAAAAACAAAABvCwAAW0NvbnRlbnRf" +
            "VHlwZXNdLnhtbAoAIAAAAAAAAQAYAABAstNc56gBAAAAAAAAAAAAAAAAAAAAAFBLAQI/ABQAAAAAAE9aBl0AAAAAAAAAAAAAAAAGACQAAAAAAAAAEAAAAN8MAABfcmVscy8KACAAAAAAAAEAGACYmK5AUiXdAQAAAAAAAAAAAAAAAAAAAABQSwECPwAUAAAACAAAACEAtVUwI+sAAABMAgAACwAkAAAAAAAAACAAAAADDQAAX3JlbHMvLnJlbHMK" +
            "ACAAAAAAAAEAGAAAQLLTXOeoAQAAAAAAAAAAAAAAAAAAAABQSwUGAAAAAAkACQBMAwAAFw4AAAAA";


    final static String ref = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\"><Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/><Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/theme\" Target=\"theme/theme1.xml\"/>$(refs)</Relationships>";
    final static String refs = "$(refs)";


    final static String workBook = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\" mc:Ignorable=\"x15\" xmlns:x15=\"http://schemas.microsoft.com/office/spreadsheetml/2010/11/main\"><fileVersion appName=\"xl\" lastEdited=\"6\" lowestEdited=\"6\" rupBuild=\"14420\"/><workbookPr defaultThemeVersion=\"164011\"/><bookViews><workbookView xWindow=\"0\" yWindow=\"0\" windowWidth=\"22260\" windowHeight=\"12645\"/></bookViews><sheets>$(sheets)</sheets><calcPr calcId=\"162913\"/><extLst><ext uri=\"{140A7094-0E35-4892-8432-C4D2E57EDEB5}\" xmlns:x15=\"http://schemas.microsoft.com/office/spreadsheetml/2010/11/main\"><x15:workbookPr chartTrackingRefBase=\"1\"/></ext></extLst></workbook>";
    final static String sheets = "$(sheets)";

    final static String app = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<Properties xmlns=\"http://schemas.openxmlformats.org/officeDocument/2006/extended-properties\" xmlns:vt=\"http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes\"><Application>Microsoft Excel</Application><DocSecurity>0</DocSecurity><ScaleCrop>false</ScaleCrop><HeadingPairs><vt:vector size=\"2\" baseType=\"variant\"><vt:variant><vt:lpstr>工作表</vt:lpstr></vt:variant><vt:variant><vt:i4>1</vt:i4></vt:variant></vt:vector></HeadingPairs><TitlesOfParts>$(appSheets)</TitlesOfParts><Company></Company><LinksUpToDate>false</LinksUpToDate><SharedDoc>false</SharedDoc><HyperlinksChanged>false</HyperlinksChanged><AppVersion>16.0300</AppVersion></Properties>";
    final static String appSheets = "$(appSheets)";
}
