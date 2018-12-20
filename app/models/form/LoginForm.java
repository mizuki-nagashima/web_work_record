package models.form;

import play.data.validation.Constraints;

/**
 * Created by suzuki-daisuke on 2017/04/07.
 */

/**
 * ログイン用フォーム
 */
public class LoginForm {
    @Constraints.Required(message = "社員番号を入力してください。")
    @Constraints.Pattern(value = "^[0-9]+$", message = "社員番号は半角数字で入力してください。")
    public String employeeNo;
    @Constraints.Required(message = "パスワードを入力してください。")
    @Constraints.MinLength(value = 8, message = "8文字以上で入力してください。")
    @Constraints.Pattern(value = "^[a-zA-Z0-9]+$", message = "パスワードは半角英数字で入力してください。")
    public String password;
    public Integer loginNgCount =0;
    public String isAccountLock = "0";
    public String isDelete = "0";
}