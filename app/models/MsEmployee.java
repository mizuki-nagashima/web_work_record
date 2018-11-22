package models;

import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

/**
 * 社員マスタ
 * 最新の状態。年月属性は本テーブルのデータをコピーして作成。
 * 最新年月属性に更新があった場合は、本テーブルも更新する。
 */
@Entity
public class MsEmployee extends CommonModel {

    /**
     * 社員番号
     */
    @NotNull
    public String employeeNo;

    /**
     * 社員氏名
     */
    @NotNull
    public String employeeName;

    /**
     * 社員氏名カナ
     */
    @NotNull
    public String employeeNameKana;

    /**
     * 役職コード
     */
    public String positionCode;

    /**
     * 部署コード
     */
    public String departmentCode;

    /**
     * 課コード
     */
    public String divisionCode;

    /**
     * 雇用区分
     */
    @NotNull
    public String employmentClass;

    /**
     * 業務コード
     */
    @NotNull
    public String businessCode;

    /**
     * 業務チームコード
     */
    @NotNull
    public String businessTeamCode;

    /**
     * 作業内訳名1
     */
    @NotNull
    public String breakdownName1;

    /**
     * 作業内訳名2
     */
    @NotNull
    public String breakdownName2;

    /**
     * 作業内訳名3
     */
    @NotNull
    public String breakdownName3;

    /**
     * 作業内訳名4
     */
    @NotNull
    public String breakdownName4;

    /**
     * 権限
     */
    @NotNull
    public String authorityClass;

    /**
     * 社員登録有無チェック
     * @param empNo 社員番号
     * @param pass パスワード
     * @return boolean
     */
    public static boolean isRegistEmp(String empNo) {
        String sql = "SELECT COUNT(*) as cnt FROM MS_EMPLOYEE " +
        		"WHERE EMPLOYEE_NO = :emp";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp", empNo)
                .findList();

        if (sqlRows.get(0).getInteger("cnt") >= 1) {
            return true;
        }
        return false;
    }

    /**
     * 社員情報取得
     * @param empNo 社員番号
     * @return sqlRow
     */
    public static SqlRow getEmployeeInfo(String empNo) {
        String sql = "SELECT * FROM MS_EMPLOYEE EMP WHERE EMP.EMPLOYEE_NO = :empNo";
        SqlRow row = null;

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("empNo", empNo)
                .findList();
        for(SqlRow o : sqlRows) {
            row = o;
        }
        return row;
    }

}
