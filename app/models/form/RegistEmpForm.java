package models.form;

import play.data.validation.Constraints;

/**
 * Created by suzuki-daisuke on 2017/04/07.
 */

/**
 * ログイン用フォーム
 */
public class RegistEmpForm {
    @Constraints.Required(message = "社員番号を入力してください。")
    @Constraints.Pattern(value = "^[0-9]+$", message = "社員番号は半角数字のみで入力してください。")
    public String employeeNo;
    @Constraints.Required(message = "社員名を入力してください。")
    @Constraints.Pattern(value = "^[^ -~｡-ﾟ]*$", message = "社員名は全角のみで入力してください。")
    public String employeeName;
    @Constraints.Required(message = "社員名カナを入力してください。")
    @Constraints.Pattern(value = "^[ァ-ヶー]*$", message = "社員名カナは全角カナのみで入力してください。")
    public String employeeNameKana;
    public String authorityClass;
    public String employmentClass;
    public String positionCode;
    public String departmentCode;
    public String divisionCode;
    public String businessCode;
    public String businessTeamCode;
    public String breakdownName1;
    public String breakdownName2;
    public String breakdownName3;
    public String breakdownName4;
    public String retirementDate = "";
}
