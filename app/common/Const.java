package common;

/**
 * 定数クラス。enum化したい。
 * @author kmsuser
 */
public class Const {

    private Const() {}

    /** デフォルトコード(該当なし) */
    public static final String DEFAULT_CODE = "00";

    /** デフォルト作業内訳名1 */
    public static final String DEFAULT_BREAKDOWN_NAME1 = "開発/保守/維持";
    /** デフォルト作業内訳名2  */
    public static final String DEFAULT_BREAKDOWN_NAME2 = "調査/分析";
    /** デフォルト作業内訳名3  */
    public static final String DEFAULT_BREAKDOWN_NAME3 = "資料作成";
    /** デフォルト作業内訳名4  */
    public static final String DEFAULT_BREAKDOWN_NAME4 = "その他";

    /** 休暇区分コード名 */
    public static final String HOLIDAY_CODE_NAME = "HOLIDAY_CLASS";
    /** シフト区分コード名 */
    public static final String SHIFT_CODE_NAME = "SHIFT_CLASS";
    /** 権限コード名 */
    public static final String AUTHORITY_CODE_NAME = "AUTHORITY_CLASS";
    /** 社員コード名 */
    public static final String EMPLOYMENT_CODE_NAME = "EMPLOYMENT_CLASS";
    /** 役職コード名 */
    public static final String POSITION_CODE_NAME = "POSITION_CODE";
    /** 部署コード名 */
    public static final String DEPARTMENT_CODE_NAME = "DEPARTMENT_CODE";
    /** 課コード名 */
    public static final String DIVISION_CODE_NAME = "DIVISION_CODE";
    /** 業務コード名 */
    public static final String BUSINESS_CODE_NAME = "BUSINESS_CODE";
    /** 業務チームコード名 */
    public static final String BUSINESS_TEAM_CODE_NAME = "BUSINESS_TEAM_CODE";

    /** 休暇区分：休日出勤 */
    public static final String HOLIDAY_CLASS_HOLIDAY_WORK = "01";
    /** 休暇区分：代休**/
    public static final String HOLIDAY_CLASS_CUMP_DAY = "02";
    /** 休暇区分：半休 */
    public static final String HOLIDAY_CLASS_HALF_HOLIDAY = "10";

    /** 権限：システム管理者権限 */
    public static final String AUTHORITY_CLASS_SYSTEM = "01";
    /** 権限：管理部権限 */
    public static final String AUTHORITY_CLASS_MANAGEMENT = "02";
    /** 権限：承認権限 */
    public static final String AUTHORITY_CLASS_APPROVAL = "03";
    /** 権限：閲覧権限 */
    public static final String AUTHORITY_CLASS_CHECK = "04";
    /** 権限：自身のみ閲覧可 */
    public static final String AUTHORITY_CLASS_SELF = "05";

    /** 年月別ステータス：未確定 */
    public static final String MONTHS_YEARS_STATUS_UNFIX = "01";
    /** 年月別ステータス：確定（承認依頼済） */
    public static final String MONTHS_YEARS_STATUS_FIX = "02";
    /** 年月別ステータス：承認済（完全FIX） */
    public static final String MONTHS_YEARS_STATUS_COMPLETE = "03";

    /** 実績別ステータス：保存済（未確定） */
    public static final String PERFORMANCE_STATUS_SAVE = "01";
    /** 実績別ステータス：承認依頼済 */
    public static final String PERFORMANCE_STATUS_NEED_APPROVAL = "02";
    /** 実績別ステータス： 承認済*/
    public static final String PERFORMANCE_STATUS_APPROVED = "03";
    /** 実績別ステータス：承認不可（要修正） */
    public static final String PERFORMANCE_STATUS_APPROVAL_NOT = "04";
}
