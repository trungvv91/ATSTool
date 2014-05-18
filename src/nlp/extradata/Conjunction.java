/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.extradata;

/**
 *
 * @author Trung Conjunction for
 */
public class Conjunction {

    /**
     * Các từ trần thuật
     */
    public final static String DECLARE_WORDS[] = {
        "cho_biết",
        "khẳng_định",
        "lưu_ý",
        "nêu_rõ",
        "nhấn_mạnh",
        "phát_biểu",
        "tuyên_bố",
        ":"
    };

    /**
     * A thì bỏ phần trước Conjunction, B thì bỏ phần sau Conjunction. Chưa xét
     * được dấu "," vì bộ tag từ không nhận biết được nó là liên từ.
     */
    public final static String CONJUNCTIONS[][] = {
        {"bởi", null, "B"},
        {"bởi", "cho_nên", "A"},
        {"bởi", "nên", "A"},
        {"bởi_vì", null, "B"},
        {"bởi_vì", "cho_nên", "A"},
        {"bởi_vì", "nên", "A"},
        {"chẳng_hạn", null, "B"},
        {"chẳng_hạn_như", null, "B"},
        {"chỉ_khi", null, "B"},
        {"chỉ_khi", "thì", "A"},
        {"chính_vì", null, "B"},
        {"chính_vì", "cho_nên", "A"},
        {"chính_vì", "nên", "A"},
        //        {"cho_đến_khi", ",", "A"},
        {"cho_nên", null, "A"},
        {"có_nghĩa_là", null, "B"},
        //        {"cốt_cho", ",", "A"},
        //        {"cùng_lúc", ",", "A"},
        {"dầu_cho", "nhưng", "A"},
        {"dẫu_cho", "nhưng", "A"},
        {"do", null, "B"},
        {"do", "cho_nên", "A"},
        {"do", "nên", "A"},
        {"do_vậy", null, "A"},
        {"do_vậy_nên", null, "A"},
        {"dù", "nhưng", "A"},
        //        {"dù", ",", "A"},
        //        {"để_cho", ",", "A"},
        //        {"khi", ",", "A"},
        {"nên", null, "A"},
        {"nếu", "thì", "A"},
        {"nếu_như", "thì", "A"},
        //        {"ngay_khi", ",", "A"},
        {"nghĩa_là", null, "B"},
        {"nhờ", null, "B"},
        //        {"nhờ", ",", "A"},
        {"nhờ_có", null, "B"},
        //        {"nhờ_có", ",", "A"},
        {"nhưng", null, "A"},
        //        {"nhằm", ",", "A"},
        {"nhằm", "thì", "A"},
        {"mặc_dù", "nhưng", "A"},
        {"miễn_là", null, "B"},
        {"miễn_là", "thì", "A"},
        //        {"sau_khi", ",", "A"},
        {"thành_ra", null, "A"},
        {"thay_vì", null, "B"},
        //        {"thay_vì", ",", "A"},
        {"trong_đó", null, "B"},
        {"trừ_khi", null, "B"},
        {"trừ_khi", "thì", "A"},
        //        {"trước_khi", ",", "A"},
        {"tuy", "nhưng", "A"},
        {"tức_là", null, "B"},
        //        {"vào_lúc", ",", "A"},
        {"vì", null, "B"},
        {"vì", "cho_nên", "A"},
        {"vì", "nên", "A"},
        {"ví_dụ", null, "B"},
        {"ví_dụ_như", null, "B"},};

    public static boolean isDeclareWord(String s) {
//        String s1 = s.toLowerCase();
        for (String dw : DECLARE_WORDS) {
            if (s.equals(dw)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Thứ tự conjunction trong mảng dựa trên conjunction đầu tiên
     *
     * @param c1
     * @param c2
     * @return Chỉ số trong mảng CONJUNCTIONS. -1 nếu không tìm thấy.
     */
    public static int getConjunction(String c1, String c2) {
//        String s1 = s.toLowerCase();
        for (int i = 0; i < CONJUNCTIONS.length; i++) {
            if ((c1 == null ? CONJUNCTIONS[i][0] == null : c1.equals(CONJUNCTIONS[i][0]))
                    && (c2 == null ? CONJUNCTIONS[i][1] == null : c2.equals(CONJUNCTIONS[i][1]))) {
                return i;
            }
        }
        return -1;
    }
}
