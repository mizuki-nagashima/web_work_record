package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

import common.Const;
import play.Logger;

/**
 * 実績テーブル
 */
@Entity
public class TblPerformanceAdmin extends CommonModel {

    /**
     * 社員番号
     */
    @NotNull
    public String employeeNo;

    /**
     * 年月
     */
    @NotNull
    public String monthsYears;

    /**
     * 日
     */
    @NotNull
    public String performanceDate;

    /**
     * 始業時間
     */
    public String openingTime;

    /**
     * 終業時間
     */
    public String closingTime;

    /**
     * 作業内訳1
     */
    public Double sumBreakdown1;

    /**
     * 作業内訳2
     */
    public Double sumBreakdown2;

    /**
     * 作業内訳3
     */
    public Double sumBreakdown3;

    /**
     * 作業内訳4
     */
    public Double sumBreakdown4;

    /**
     * 実績時間
     */
    public Double performanceTime;


    /**
     * 控除（深夜）
     */
    public Double deductionNight;

    /**
     * 控除（その他）
     */
    public Double deductionOther;

    /**
     * 休暇区分
     */
    public String holidayClass;

    /**
     * シフト区分
     */
    public String shiftClass;

    /**
     * 備考
     */
    public String remarks;

    /**
     * 実績別ステータス
     */
    @NotNull
    public String performanceStatus;

    /**
     * 承認者社員番号
     */
    public String approvalEmployeeNo;

    /**
     * 承認日時
     */
    public Timestamp approvalDate;

    /**
     * 実績データ取得
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRows
     */
    public static List<SqlRow> getPerformanceData(String empNo, String yearMonth) {
        String sql = "SELECT * FROM TBL_PERFORMANCE PER LEFT JOIN MS_GENERAL_CODE HOL ON PER.HOLIDAY_CLASS = HOL.CODE " +
                     "AND CODE_TYPE = 'HOLIDAY_CLASS' " +
                     "WHERE PER.EMPLOYEE_NO = :empNo AND PER.MONTHS_YEARS = :yearmonth";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("empNo", empNo)
                .setParameter("yearmonth", yearMonth)
                .findList();

        return sqlRows;
    }

    /**
     * 実績データ取得、引数で渡された、日付のデータをかえします
     * @param empNum 社員番号
     * @param yearMonth 年月
     * @param date 日
     * @return sqlRows
     */
    public static List<SqlRow> getPerformanceDataByYearMonthAndDate(String empNo, String yearMonth, String date) {
        String sql = "SELECT * FROM TBL_PERFORMANCE PER LEFT JOIN MS_GENERAL_CODE HOL " +
                "ON PER.HOLIDAY_CLASS = HOL.CODE AND HOL.CODE_TYPE = 'HOLIDAY_CLASS' " +
                "WHERE PER.EMPLOYEE_NO = :emp AND PER.MONTHS_YEARS = :yearmonth AND PER.PERFORMANCE_DATE = :date";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp",empNo)
                .setParameter("yearmonth",yearMonth)
                .setParameter("date",date)
                .findList();

        return sqlRows;
    }

    /**
     * 引数で渡された年月の実績が記録してある日付データを取得します
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return ArrayList<String>
     */
    public static ArrayList<String> getPerformanceDataList(String empNo, String yearMonth) {
        ArrayList<String> performanceDataList = new ArrayList<>();
        for(SqlRow pd : getPerformanceData(empNo, yearMonth)){
            performanceDataList.add(pd.getString("performance_date"));
        }
        return performanceDataList;
    }

    /**
     * 実績データから承認が必要なデータを承認済み以外で取得します
     * @param performanceData 実績テーブル
     * @return sqlRows
     */
    public static List<SqlRow> getApproveNecessaryData(TblPerformanceAdmin performanceData) {
        String sql = "SELECT * FROM TBL_PERFORMANCE T " +
        		"WHERE T.EMPLOYEE_NO = :emp AND T.MONTHS_YEARS = :yearmonth " +
        		"AND ((T.OPENING_TIME != :open OR T.CLOSING_TIME != :close ) " +
        		" OR (T.HOLIDAY_CLASS != :holiday OR T.SHIFT_CLASS != :shift)) " +
        		"AND T.PERFORMANCE_STATUS != :status " +
        		"ORDER BY T.PERFORMANCE_DATE";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp",performanceData.employeeNo)
                .setParameter("yearmonth",performanceData.monthsYears)
                .setParameter("open",performanceData.openingTime)
                .setParameter("close",performanceData.closingTime)
                .setParameter("holiday",performanceData.holidayClass)
                .setParameter("shift",performanceData.shiftClass)
                .setParameter("status",Const.PERFORMANCE_STATUS_APPROVED)
                .findList();

        return sqlRows;
    }

    /**
     * 社員番号とステータスからデータリストを取得します
     * @param empNo
     * @param status
     * @return
     */
    public static List<SqlRow> getPerformanceDataByStatus(String empNo,String status) {
        String sql = "SELECT * FROM TBL_PERFORMANCE T " +
        		"WHERE T.EMPLOYEE_NO = :emp " +
        		"AND  T.PERFORMANCE_STATUS = :status";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp",empNo)
                .setParameter("status",status)
                .findList();

        return sqlRows;
    }

    /**
     * 実績データ登録
     * @param performanceData 実績テーブル
     */
    public static void insertPerformanceData(TblPerformanceAdmin performanceData) throws Exception {
        Ebean.beginTransaction();
        try {
            performanceData.insert();
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("実績データ登録失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * 実績データ更新
     * @param performanceData 実績テーブル
     */
    public static void updatePerformanceData(TblPerformanceAdmin performanceData) throws Exception {
        String sql = "UPDATE TBL_PERFORMANCE SET OPENING_TIME = :opt,CLOSING_TIME = :clt,sumBreakdown1 = :break1," +
                     "sumBreakdown2 = :break2,sumBreakdown3 = :break3,sumBreakdown4 = :break4," +
                     "PERFORMANCE_TIME = :pertime,DEDUCTION_NIGHT = :den,DEDUCTION_OTHER = :deo," +
                     "HOLIDAY_CLASS = :holiday,SHIFT_CLASS = :shift,PERFORMANCE_STATUS = :status," +
                     "REMARKS = :rem,UPDATE_USER_ID =:upuser, UPDATE_DATE = CURRENT_TIMESTAMP " +
                     "WHERE EMPLOYEE_NO = :emp AND MONTHS_YEARS = :yearmonth AND PERFORMANCE_DATE = :date";
        Ebean.beginTransaction();
        try {
        SqlUpdate create = Ebean.createSqlUpdate(sql)
                .setParameter("opt",performanceData.openingTime)
                .setParameter("clt",performanceData.closingTime)
                .setParameter("break1",performanceData.sumBreakdown1)
                .setParameter("break2",performanceData.sumBreakdown2)
                .setParameter("break3",performanceData.sumBreakdown3)
                .setParameter("break4",performanceData.sumBreakdown4)
                .setParameter("pertime",performanceData.performanceTime)
                .setParameter("den",performanceData.deductionNight)
                .setParameter("deo",performanceData.deductionOther)
                .setParameter("holiday",performanceData.holidayClass)
                .setParameter("shift",performanceData.shiftClass)
                .setParameter("status",performanceData.performanceStatus)
                .setParameter("rem",performanceData.remarks)
                .setParameter("upuser",performanceData.updateUserId)
                .setParameter("emp",performanceData.employeeNo)
                .setParameter("yearmonth",performanceData.monthsYears)
                .setParameter("date",performanceData.performanceDate);

            Ebean.execute(create);
            Ebean.commitTransaction();
        } catch (Exception e) {
            // debug
            System.out.println("実績データ更新失敗："+ e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * 実績データ削除
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @param date 日
     */
    public static void deletePerformanceData(String empNo, String yearMonth, String date) {
        String sql = "DELETE FROM TBL_PERFORMANCE WHERE EMPLOYEE_NO = :emp AND MONTHS_YEARS = :yearmonth AND PERFORMANCE_DATE = :date";
        Ebean.beginTransaction();
        try {
	        SqlUpdate create = Ebean.createSqlUpdate(sql)
	                .setParameter("emp",empNo)
	                .setParameter("yearmonth",yearMonth)
	                .setParameter("date",date);
	        Ebean.execute(create);
	        Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("実績データ削除失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

    /**
     * 承認一覧情報取得
     * @param businessTeamCode 業務チームコード
     * @param yearMonth 年月
     */
    public static List<SqlRow> getApproveList(List<String> businessTeamCodeList, String yearMonth) {
    	String sql = "SELECT PER.OPENING_TIME AS OPN_TIME, PER.CLOSING_TIME AS CLO_TIME, YMA.BUSINESS_CODE AS BS_CODE, PER.EMPLOYEE_NO AS EMP_NO, EMP.EMPLOYEE_NAME AS EMP_NAME, PER.MONTHS_YEARS AS MON_YR, PER.PERFORMANCE_DATE AS PER_DATE, PER.HOLIDAY_CLASS AS HO_CL, PER.SHIFT_CLASS AS SHI_CL , " +
    				 "PER.REMARKS AS REM, PER.PERFORMANCE_STATUS AS PER_ST, PER.APPROVAL_EMPLOYEE_NO AS APP_EMP_NO, PER.APPROVAL_DATE AS APP_DATE, APPEMP.POSITION_CODE AS APP_EMP_POSITION, APPEMP.EMPLOYEE_NAME AS APP_EMP_NAME, " +
    				 "YMA.MONTHS_YEARS_STATUS AS MON_YR_ST " +
    				 "FROM TBL_PERFORMANCE PER INNER JOIN MS_EMPLOYEE EMP JOIN TBL_YEAR_MONTH_ATTRIBUTE YMA ON PER.EMPLOYEE_NO = EMP.EMPLOYEE_NO AND PER.EMPLOYEE_NO = YMA.EMPLOYEE_NO " +
    				 "LEFT OUTER JOIN ( SELECT EMPLOYEE_NO, EMPLOYEE_NAME, POSITION_CODE FROM MS_EMPLOYEE ) APPEMP ON PER.APPROVAL_EMPLOYEE_NO = APPEMP.EMPLOYEE_NO " +
    				 "WHERE YMA.MONTHS_YEARS_STATUS IN ('02', '03') AND PER.PERFORMANCE_STATUS != :status AND PER.MONTHS_YEARS = :yearmonth AND YMA.BUSINESS_TEAM_CODE IN (:btc) " +
    				 "GROUP BY PER_DATE  ORDER BY EMP_NO , PER_DATE";
//    	String sql = "SELECT * FROM TBL_PERFORMANCE T " +
//    			"WHERE  T.MONTHS_YEARS = :yearmonth " +
//    			"AND T.PERFORMANCE_STATUS != :status";

    	List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
    			.setParameter("yearmonth" ,yearMonth)
    			.setParameter("status" ,Const.PERFORMANCE_STATUS_SAVE)
    			.setParameter("btc" ,businessTeamCodeList)
    			.findList();

//    	Logger.debug("sql:" + String.valueOf(sql));

    	return sqlRows;
    }

    /**
     * 承認ステータス変更
     * @param empNo 社員番号　
     * @param yearMonth 年月
     * @param date 日
     * @param perStatus 実績ステータス
     * @param appemp 承認者社員番号
     */
    public static void updateApprove(String empNo, String yearMonth, String date, String perStatus, String appemp) {
    	String sql = "update tbl_performance set performance_status = :perStatus, " +
    		   	     "approval_employee_no = :appEmp, approval_date = current_timestamp ,"
    		   	     + "update_user_id=:appEmp, update_date=current_timestamp " +
    				 "where employee_no = :emp and months_years = :yearmonth and performance_date = :date";
        Ebean.beginTransaction();
        try {
	    	SqlUpdate create = Ebean.createSqlUpdate(sql)
	    		.setParameter("perStatus",perStatus)
	    		.setParameter("appEmp",appemp)
		        .setParameter("emp",empNo)
		        .setParameter("yearmonth",yearMonth)
		        .setParameter("date",date);
	        Ebean.execute(create);
	        Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("承認処理失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }

    /**
     * 属性変更（承認以外）
     * @param empNo 社員番号　
     * @param yearMonth 年月
     * @param date 日
     * @param perStatus 実績ステータス
     */
    public static void updateApprove(String empNo, String yearMonth, String date, String perStatus) {
    	String sql = "update tbl_performance set performance_status = :perStatus, " +
    			"update_user_id=:emp, update_date=current_timestamp " +
    				 "where employee_no = :emp and months_years = :yearmonth and performance_date = :date";
        Ebean.beginTransaction();
        try {
	    	SqlUpdate create = Ebean.createSqlUpdate(sql)
	    		.setParameter("perStatus",perStatus)
		        .setParameter("emp",empNo)
		        .setParameter("yearmonth",yearMonth)
		        .setParameter("date",date);
	        Ebean.execute(create);
	        Ebean.commitTransaction();
	    } catch (Exception e) {
	        // debug
	        System.out.println("承認ステータス変更処理失敗："+ e);
	        Ebean.rollbackTransaction();
	        throw e;
	    } finally {
	        Ebean.endTransaction();
	    }
    }
}
