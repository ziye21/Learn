package constant;

/**
 * hanlp、ltp、thulac与北大词性对比
 * @author yangshuai
 * @date 2018/10/22 17:33
 */
public enum WordType {
    AG("ag","","","Ag","形语素"),
    A("a","a","a","a","形容词"),
    AL("al","","","a","形容词"),//
    AD("ad","","","ad","副形词"),
    AN("an","","","an","名形词"),
    B("b","","","b","区别词"),
    BEGIN("begin","","","b","区别词"),//
    BG("bg","","","b","区别词"),//
    BL("bl","","","b","区别词"),//
    C("c","c","c","c","连词"),
    CC("cc","","","c","连词"),//
    DG("dg","","","Dg","副语素"),
    D("d","d","d","d","副词"),
    DL("dl","","","d","副词"),//
    E("e","e","e","e","叹词"),
    END("end","","","e","叹词"),//
    F("f","nd","f","f","方位词"),
    G("g","g","g","g","语素"),//hanlp学术词汇
    H("h","h","h","h","前接成分"),
    I("i","i","i","i","成语"),//thulac习惯用语
    J("j","j","j","j","简称略语"),
    K("k","k","k","k","后接成分"),
    L("l","","","l","习用语"),
    M("m","m","m","m","数词"),
    mg("mg","","","m","数词"),//
    Mg("Mg","","","m","数词"),//
    MQ("mq","","mq","m","数词"),//
    NG("ng","","","Ng","名语素"),
    N("n","n","n","n","名词"),
    NR("nr","nh","np","nr","人名"),
    NS("ns","ns","ns","ns","地名"),
    NT("nt","ni","ni","nt","机构团体"),
    NZ("nz","nz","nz","nz","其他专名"),
    NB("nb","nt","","nz","其他专名"),//
    NBA("nba","nl","","nz","其他专名"),//
    NBC("nbc","","","nz","其他专名"),//
    NBP("nbp","","","nz","其他专名"),//
    NF("nf","","","nz","其他专名"),//
    NH("nh","","","nz","其他专名"),//
    NHD("nhd","","","nz","其他专名"),//
    NHM("nhm","","","nz","其他专名"),//
    NI("ni","ni","","nz","其他专名"),//
    NIC("nic","","","nz","其他专名"),//
    NIS("nis","","","nz","其他专名"),//
    NL("nl","","","nz","其他专名"),//
    NM("nm","","","nz","其他专名"),//
    NMC("nmc","","","nz","其他专名"),//
    NN("nn","","","nz","其他专名"),//
    NND("nnd","","","nz","其他专名"),//
    NNT("nnt","","","nz","其他专名"),//
    NR1("nr1","","","nr","人名"),//
    NR2("nr2","","","nr","人名"),//
    NRF("nrf","","","nr","人名"),//
    NRJ("nrj","","","nr","人名"),//
    NSF("nsf","","","ns","地名"),//
    NTC("ntc","","","nt","机构团体"),//
    NTCB("ntcb","","","nt","机构团体"),//
    NTCF("ntcf","","","nt","机构团体"),//
    NTCH("ntch","","","nt","机构团体"),//
    NTH("nth","","","nt","机构团体"),//
    NTO("nto","","","nt","机构团体"),//
    NTS("nts","","","nt","机构团体"),//
    NTU("ntu","","","nt","机构团体"),//
    NX("nx","","","nt","其他专名"),//
    O("o","o","o","o","拟声词"),
    P("p","p","p","p","介词"),
    PBA("pba","","","p","介词"),//
    PBEI("pbei","","","p","介词"),//
    Q("q","q","q","q","量词"),
    QG("qg","","","q","量词"),//
    QT("qt","","","q","量词"),//
    QV("qv","","","q","量词"),//
    R("r","r","r","r","代词"),
    RG("rg","","","r","代词"),//
    Rg("Rg","","","r","代词"),//
    RR("rr","","","r","代词"),//
    RY("ry","","","r","代词"),//
    RYS("rys","","","r","代词"),//
    RYT("ryt","","","r","代词"),//
    RYV("ryv","","","r","代词"),//
    RZ("rz","","","r","代词"),//
    RZS("rzs","","","r","代词"),//
    RZT("rzt","","","r","代词"),//
    RZV("rzv","","","r","代词"),//
    S("s","","s","s","处所词"),
    TG("tg","","","Tg","时语素"),
    T("t","","t","t","时间词"),
    U("u","u","u","u","助词"),
    UD("ud","","","u","助词"),//
    UDE1("ude1","","","u","助词"),//
    UDE2("ude2","","","u","助词"),//
    UDE3("ude3","","","u","助词"),//
    UDENG("udeng","","","u","助词"),//
    UDH("udh","","","u","助词"),//
    UG("ug","","","u","助词"),//
    UGUO("uguo","","","u","助词"),//
    UJ("uj","","","u","助词"),//
    UL("ul","","","u","助词"),//
    ULE("ule","","","u","助词"),//
    ULIAN("ulian","","","u","助词"),//
    ULS("uls","","","u","助词"),//
    USUO("usuo","","","u","助词"),//
    UV("uv","","","u","助词"),//
    UYY("uyy","","","u","助词"),//
    UZ("uz","","","u","助词"),//
    UZHE("uzhe","","","u","助词"),//
    UZHI("uzhi","","","u","助词"),//
    VG("vg","","","Vg","动语素"),
    V("v","v","v","v","动词"),//
    VF("vf","","vd","v","动词"),//
    VI("vi","","vm","v","动词"),//
    VL("vl","","","v","动词"),//
    VSHI("vshi","","","v","动词"),//
    VX("vx","","","v","动词"),//
    VYOU("vyou","","","v","动词"),//
    VD("vd","","","vd","副动词"),
    VN("vn","","","vn","名动词"),
    W("w","","w","w","标点符号"),
    WB("wb","","","w","标点符号"),//
    WD("wd","","","w","标点符号"),//
    WF("wf","","","w","标点符号"),//
    WH("wh","","","w","标点符号"),//
    WJ("wj","","","w","标点符号"),//
    WKY("wky","","","w","标点符号"),//
    WKZ("wkz","","","w","标点符号"),//
    WM("wm","","","w","标点符号"),//
    WN("wn","","","w","标点符号"),//
    WP("wp","","","w","标点符号"),//
    WS("ws","","","w","标点符号"),//
    WT("wt","","","w","标点符号"),//
    WW("ww","","","w","标点符号"),//
    WYY("wyy","","","w","标点符号"),//
    WYZ("wyz","","","w","标点符号"),//
    X("x","x","x","x","非语素字"),//
    XU("xu","ws","","x","非语素字"),//
    XX("xx","","","x","非语素字"),//
    Y("y","","y","y","语气词"),
    YG("yg","","","y","语气词"),//
    Z("z","","","z","状态词"),
    ZG("zg","","","z","状态词");//

    /** hanlp */
    private String hCode;

    /** ltp */
    private String lCode;

    /** thulac */
    private String tCode;

    /** 清华词性 */
    private String code;

    /** 描述 */
    private String desc;

    WordType(String hCode, String lCode, String tCode, String code, String desc) {
        this.hCode = hCode;
        this.lCode = lCode;
        this.tCode = tCode;
        this.code = code;
        this.desc = desc;
    }

    public String gethCode() {
        return hCode;
    }

    public String getlCode() {
        return lCode;
    }

    public String gettCode() {
        return tCode;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * ltp
     * @param lCode
     * @return
     */
    public static String getByLcode(String lCode) {
        for (WordType w : WordType.values()) {
            if (w.lCode.equals(lCode)) {
                return w.code;
            }
        }
        return lCode;
    }

    /**
     * hanlp
     * @param hCode
     * @return
     */
    public static String getByHcode(String hCode) {
        for (WordType w : WordType.values()) {
            if (w.hCode.equals(hCode)) {
                return w.code;
            }
        }
        return hCode;
    }

    /**
     * thulac
     * @param tCode
     * @return
     */
    public static String getByTcode(String tCode) {
        for (WordType w : WordType.values()) {
            if (w.tCode.equals(tCode)) {
                return w.code;
            }
        }
        return tCode;
    }

}
