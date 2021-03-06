package models;

import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

import play.Logger;

/**
 * 社員業務管理テーブル
 */
@Entity
public class MsPerformanceManage extends CommonModel {

    /**
     * 開始日
     */
    @NotNull
    public String startDate;

    /**
     * 終了日
     */
    @NotNull
    public String endDate;

    /**
     * 社員番号
     */
    @NotNull
    public String employeeNo;

    /**
     * 業務コード
     */
    public String businessCode;

    /**
     * 業務チームコード
     */
    public String businessTeamCode;

    /**
     * 権限
     */
    public String businessManageAuthClass;

    /**
     * 社員業務管理マスタ取得
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRows
     */
    public static List<SqlRow> getMsPerManage(String empNo) {
        String sql = "SELECT * FROM MS_PERFORMANCE_MANAGE " +
                     "WHERE EMPLOYEE_NO = :emp AND START_DATE <= CURRENT_TIMESTAMP "
                     + "AND END_DATE > CURRENT_TIMESTAMP";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        		.setParameter("emp" ,empNo)
                .findList();

 //       Logger.debug("sql:" + String.valueOf(sqlRows));

        return sqlRows;
    }

    /**
     * 社員番号と業務コードからデータ取得
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRowsgetMsPerformaceByCodeName
     */
    public static List<SqlRow> getMsPerformaceByCodeName(String empNo,String codeName, List<String> code) {
        String sql = "SELECT * FROM MS_PERFORMANCE_MANAGE " +
        		"WHERE EMPLOYEE_NO=:emp AND START_DATE <= CURRENT_TIMESTAMP " +
        		"AND :codeName IN(:code) " ;

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        		.setParameter("emp" ,empNo)
        		.setParameter("codeName" ,codeName)
        		.setParameter("code" ,code)
                .findList();

        return sqlRows;
    }

    /**
     * 社員番号からコードを取得
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRows
     */
    public static List<SqlRow> getBusCodeByEmpNo(String empNo) {
        String sql = "SELECT DISTINCT * FROM MS_PERFORMANCE_MANAGE " +
        		"WHERE EMPLOYEE_NO = :emp AND START_DATE <= CURRENT_TIMESTAMP " +
        		"AND END_DATE > CURRENT_TIMESTAMP";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        		.setParameter("emp" ,empNo)
                .findList();

//        Logger.debug("sql:" + String.valueOf(sql));

        return sqlRows;
    }

    /**
     * 社員業務管理テーブル登録
     * @param empInfo 社員情報
     */
    public static void insertMsPerManage(MsPerformanceManage msManage) throws Exception {
        Ebean.beginTransaction();
        try {
        	msManage.insert();
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("社員業務管理テーブル登録失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * 社員業務管理テーブル削除
     * @param empNo
     */
    public static void deleteMsPerManage(String empNo) {
        String sql = "DELETE FROM MS_PERFORMANCE_MANAGE WHERE EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
	        SqlUpdate create = Ebean.createSqlUpdate(sql)
	                .setParameter("emp",empNo);
	        Ebean.execute(create);
	        Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("社員業務管理テーブル削除失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

    /**
     * 終了日を本日で更新
     * @param empNo
     */
    public static void updateEndDate(String empNo,String busCode,String busTeamCode) {
        String sql = "UPDATE MS_PERFORMANCE_MANAGE " +
        		"SET END_DATE = CURRENT_DATE " +
        		"WHERE EMPLOYEE_NO = :emp AND BUSINESS_CODE = :bus AND BUSINESS_TEAM_CODE = :busteam";
        Ebean.beginTransaction();
        try {
            SqlUpdate create = Ebean.createSqlUpdate(sql)
            .setParameter("emp",empNo)
            .setParameter("bus",busCode)
            .setParameter("busteam",busTeamCode);

            Ebean.execute(create);
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("社員業務管理テーブルデータ更新失敗："+ e);
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
    public static void updateMsPerManage(MsPerformanceManage msManage) {
        String sql = "UPDATE MS_PERFORMANCE_MANAGE MS " +
        		"SET MS.BUSINESS_CODE=:bus,MS.BUSINESS_TEAM_CODE=:busteam " +
        		"WHERE MS.EMPLOYEE_NO = :emp";
        Ebean.beginTransaction();
        try {
            SqlUpdate create = Ebean.createSqlUpdate(sql)
            .setParameter("emp",msManage.employeeNo)
            .setParameter("bus",msManage.businessCode)
            .setParameter("busteam",msManage.businessTeamCode);

            Ebean.execute(create);
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("社員業務管理テーブルデータ更新失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

}
