package models;

import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

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
    public String password;

    /**
     * ログインNG回数
     */
    public Integer loginNgCount;

    /**
     * アカウントロック
     */
    public String isAccountLock;

    /**
     * 削除フラグ
     */
    public String isDelete;

    /**
     * ログインチェック
     * @param empNo 社員番号
     * @param pass パスワード
     * @return boolean
     */
    public static boolean isLogin(String empNo, String pass) {
        String sql = "SELECT COUNT(*) as cnt FROM TBL_LOGIN_INFO " +
                "WHERE EMPLOYEE_NO = :emp AND PASSWORD = :pass AND IS_DELETE = 0";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp", empNo)
                .setParameter("pass", pass)
                .findList();

        if (sqlRows.get(0).getInteger("cnt") >= 1) {
            return true;
        }
        return false;
    }

    /**
     * ログイン情報取得
     * @param empNo 社員番号
     * @param pass パスワード
     * @return Sqlrow
     */
    public static SqlRow getLoginInfo(String empNo) {
        String sql = "SELECT * FROM TBL_LOGIN_INFO " +
                "WHERE EMPLOYEE_NO = :emp";
        SqlRow row = null;

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp", empNo)
                .findList();

        for(SqlRow o : sqlRows) {
            row = o;
        }
 //       System.out.println("sql:" + String.valueOf(sqlRows));
        return row;
    }

    /**
     * ログイン情報登録
     * @param tblInfo ログイン情報
     */
    public static void insertTblInfo(TblLoginInfo tblInfo) throws Exception {
        Ebean.beginTransaction();
        try {
        	tblInfo.insert();
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("ログイン情報登録失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * ログインNG回数カウントアップ
     * @param empNo 社員番号
     * @param loginNgCount  ログインNG回数
     */
    public static void loginNgCountUp(String empNo, int loginNgCount) {
        int count = loginNgCount + 1;

        String sql = "UPDATE TBL_LOGIN_INFO SET " +
        		"LOGIN_NG_COUNT = :count " +
                "WHERE EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
	        SqlUpdate create = Ebean.createSqlUpdate(sql)
	                .setParameter("emp", empNo)
	                .setParameter("count", count);
	        Ebean.execute(create);
	        Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("ログインNG回数カウント更新失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

    /**
     * アカウントロック更新
     * @param empNo 社員番号
     * @param loginNgCount  ログインNG回数
     */
    public static void accountLock(String empNo, String flg) {
        String sql = "UPDATE TBL_LOGIN_INFO SET " +
        		"IS_ACCOUNT_LOCK = :lock " +
                "WHERE EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
	        SqlUpdate create = Ebean.createSqlUpdate(sql)
	                .setParameter("emp", empNo)
	                .setParameter("lock", flg);
		   Ebean.execute(create);
		   Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("アカウントロック更新失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

    /**
     * 削除フラグ更新
     * @param empNo 社員番号
     * @param loginNgCount  ログインNG回数
     */
    public static void updateDeleteFlg(String empNo, String flg) {
        String sql = "UPDATE TBL_LOGIN_INFO SET " +
        		"IS_DELETE = :flg " +
                "WHERE EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
	        SqlUpdate create = Ebean.createSqlUpdate(sql)
	                .setParameter("emp", empNo)
	                .setParameter("flg", flg);
		   Ebean.execute(create);
		   Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("削除フラグ更新失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

    /**
     * ログイン情報の物理削除
     * @param empNo
     */
    public static void deleteTblInfo(String empNo) {
        String sql = "DELETE FROM TBL_LOGIN_INFO WHERE EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
	        SqlUpdate create = Ebean.createSqlUpdate(sql)
	                .setParameter("emp",empNo);
	        Ebean.execute(create);
	        Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("ログイン情報削除失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

}
