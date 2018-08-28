package models;

import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

/**
 * ログイン情報
 */
@Entity
public class TblLoginInfo extends CommonModel {

    /**
     * 社員番号
     */
    @NotNull
    public String employeeNo;

    /**
     * パスワード
     */
    @NotNull
    public String password;

    /**
     * ログインNG回数
     */
    @NotNull
    public String loginNgCount;

    /**
     * アカウントロック
     */
    @NotNull
    public String isAccountLock;

    /**
     * ログインチェック
     * @param empNo 社員番号
     * @param pass パスワード
     * @return boolean
     */
    public static boolean isLogin(String empNo, String pass) {
        String sql = "SELECT COUNT(*) as cnt FROM TBL_LOGIN_INFO " +
                "WHERE EMPLOYEE_NO = :emp AND PASSWORD = :pass";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp", empNo)
                .setParameter("pass", pass)
                .findList();

        if (sqlRows.get(0).getInteger("cnt") >= 1) {
            return true;
        }
        return false;
    }

}
