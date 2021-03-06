package models.form;

import java.util.List;

import common.Const;
import play.data.validation.Constraints;

/**
 * @author nagashima-mizuki
 *
 */

/**
 * 社員情報用フォーム
 */
public class RegistEmpForm {
    @Constraints.Required(message = "社員番号を入力してください。")
    @Constraints.Pattern(value = "^[0-9a-z]+$", message = "社員番号は半角英数字（協力社員は社員番号の頭に「k」がつきます）で入力してください。")
    public String employeeNo;
    @Constraints.Required(message = "社員名を入力してください。")
    @Constraints.Pattern(value = "^[^ -~｡-ﾟ]*$", message = "社員名は全角のみで入力してください。")
    public String employeeName;
    @Constraints.Required(message = "社員名カナを入力してください。")
    @Constraints.Pattern(value = "^[ァ-ヶー]*$", message = "社員名カナは全角カナのみで入力してください。")
    public String employeeNameKana;
    public String password;
    public String authorityClass;
    public String authorityClassName;
    public String employmentClass;
    public String employmentClassName;
    public String positionCode = Const.DEFAULT_CODE;
    public String positionName;
    public String departmentCode = Const.DEFAULT_CODE;
    public String departmentName;
    public String divisionCode = Const.DEFAULT_CODE;
    public String divisionName;
    @Constraints.Required(message = "業務名を選択してください。")
    public List<String> businessCode;
    public List<String> businessName;
    @Constraints.Required(message = "業務チーム名を選択してください。")
    public List<String> businessTeamCode;
    public List<String> businessTeamName;
    public String breakdownName1;
    public String breakdownName2;
    public String breakdownName3;
    public String breakdownName4;
}
