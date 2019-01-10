package models;

import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

import models.form.RegistEmpForm;

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
    @NotNull
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
    public String businessCode;

    /**
     * 業務チームコード
     */
    public String businessTeamCode;

    /**
     * 作業内訳名1
     */
    public String breakdownName1;

    /**
     * 作業内訳名2
     */
    public String breakdownName2;

    /**
     * 作業内訳名3
     */
    public String breakdownName3;

    /**
     * 作業内訳名4
     */
    public String breakdownName4;

    /**
     * 権限
     */
    @NotNull
    public String authorityClass;

    /**
     * 退職日
     */
    public String retirementDate;

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
        String sql = "SELECT * FROM MS_EMPLOYEE EMP WHERE EMP.EMPLOYEE_NO = :empNo ";
        SqlRow row = null;

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("empNo", empNo)
                .findList();
        for(SqlRow o : sqlRows) {
            row = o;
        }
        return row;
    }

    /**
     * 社員リスト取得
     * @param empNo 社員番号
     * @return sqlRow
     */
    public static List<SqlRow> getEmployeeInfoList() {
        String sql = "SELECT * FROM MS_EMPLOYEE EMP WHERE EMP.RETIREMENT_DATE IS NULL ";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .findList();
        return sqlRows;
    }

    /**
     * 社員情報登録
     * @param empInfo 社員情報
     */
    public static void insertMsEmployee(MsEmployee empInfo) throws Exception {
        Ebean.beginTransaction();
        try {
        	empInfo.insert();
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("社員マスタ登録失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * 社員情報を削除
     * @param empNo
     */
    public static void deleteMsEmployee(String empNo) {
        String sql = "DELETE FROM MS_EMPLOYEE WHERE EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
	        SqlUpdate create = Ebean.createSqlUpdate(sql)
	                .setParameter("emp",empNo);
	        Ebean.execute(create);
	        Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("社員情報削除失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

    /**
     * 退職日を本日で設定する（ユーザ論理削除）
     * @param empNo
     */
    public static void updateRetirementDate(String empNo) {
        String sql = "UPDATE MS_EMPLOYEE " +
        		"SET RETIREMENT_DATE = CURRENT_DATE " +
        		"WHERE EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
            SqlUpdate create = Ebean.createSqlUpdate(sql)
            		.setParameter("emp",empNo);

            Ebean.execute(create);
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("社員マスタ更新失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * 更新する
     * @param empNo
     */
    public static void updateMsEmployee(MsEmployee form) {
        String sql = "UPDATE MS_EMPLOYEE MS " +
        		"SET MS.EMPLOYEE_NAME=:empName, MS.EMPLOYEE_NAME_KANA=:empNameKana, " +
        		"MS.POSITION_CODE=:poCd, MS.EMPLOYMENT_CLASS=:emCl, MS.AUTHORITY_CLASS=:auCl, "+
        		"MS.DEPARTMENT_CODE=:deCd, MS.DIVISION_CODE=:diCd, " +
        		"MS.BREAKDOWN_NAME1=:bn1, MS.BREAKDOWN_NAME2=:bn2, " +
        		"MS.BREAKDOWN_NAME3=:bn3, MS.BREAKDOWN_NAME4=:bn4 " +
        		"WHERE MS.EMPLOYEE_NO=:empNo";
        Ebean.beginTransaction();
        try {
            SqlUpdate create = Ebean.createSqlUpdate(sql)
            		.setParameter("empNo",form.employeeNo)
            .setParameter("empName",form.employeeName)
            .setParameter("empNameKana",form.employeeNameKana)
            .setParameter("poCd",form.positionCode)
            .setParameter("emCl",form.employmentClass)
            .setParameter("auCl",form.authorityClass)
            .setParameter("deCd",form.departmentCode)
            .setParameter("diCd",form.divisionCode)
            .setParameter("bn1",form.breakdownName1)
            .setParameter("bn2",form.breakdownName2)
            .setParameter("bn3",form.breakdownName3)
            .setParameter("bn4",form.breakdownName4);

            Ebean.execute(create);
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("社員マスタ更新失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

}
