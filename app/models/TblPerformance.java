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
public class TblPerformance extends CommonModel {

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
    public Double breakdown1;

    /**
     * 作業内訳2
     */
    public Double breakdown2;

    /**
     * 作業内訳3
     */
    public Double breakdown3;

    /**
     * 作業内訳4
     */
    public Double breakdown4;

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
     * その他承認区分
     */
    public String other_approval_class;

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
    public static List<SqlRow> getApproveNecessaryData(TblPerformance performanceData) {
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

    public static SqlRow getPerformanceDataByStatus(String empNo, String yearMonth,String status) {
        String sql = "SELECT * FROM TBL_PERFORMANCE T " +
        		"WHERE T.EMPLOYEE_NO = :emp AND T.MONTHS_YEARS = :yearmonth " +
        		"AND  T.PERFORMANCE_STATUS = :status";

        SqlRow sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("emp",empNo)
                .setParameter("yearmonth",yearMonth)
                .setParameter("status",status)
                .findUnique();

        return sqlRows;
    }

    /**
     * 実績データ登録
     * @param performanceData 実績テーブル
     */
    public static void insertPerformanceData(TblPerformance performanceData) throws Exception {
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
    public static void updatePerformanceData(TblPerformance performanceData) throws Exception {
        String sql = "UPDATE TBL_PERFORMANCE SET OPENING_TIME = :opt,CLOSING_TIME = :clt,BREAKDOWN1 = :break1," +
                     "BREAKDOWN2 = :break2,BREAKDOWN3 = :break3,BREAKDOWN4 = :break4," +
                     "PERFORMANCE_TIME = :pertime,DEDUCTION_NIGHT = :den,DEDUCTION_OTHER = :deo," +
                     "HOLIDAY_CLASS = :holiday,SHIFT_CLASS = :shift,OTHER_APPROVAL_CLASS = :other," +
                     "PERFORMANCE_STATUS = :status, REMARKS = :rem,UPDATE_DATE = CURRENT_TIMESTAMP " +
                     "WHERE EMPLOYEE_NO = :emp AND MONTHS_YEARS = :yearmonth AND PERFORMANCE_DATE = :date";
        Ebean.beginTransaction();
        try {
        SqlUpdate create = Ebean.createSqlUpdate(sql)
                .setParameter("opt",performanceData.openingTime)
                .setParameter("clt",performanceData.closingTime)
                .setParameter("break1",performanceData.breakdown1)
                .setParameter("break2",performanceData.breakdown2)
                .setParameter("break3",performanceData.breakdown3)
                .setParameter("break4",performanceData.breakdown4)
                .setParameter("pertime",performanceData.performanceTime)
                .setParameter("den",performanceData.deductionNight)
                .setParameter("deo",performanceData.deductionOther)
                .setParameter("holiday",performanceData.holidayClass)
                .setParameter("shift",performanceData.shiftClass)
                .setParameter("other",performanceData.other_approval_class)
                .setParameter("status",performanceData.performanceStatus)
                .setParameter("rem",performanceData.remarks)
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
    	String sql = "select per.opening_time as opn_time, per.closing_time as clo_time, yma.business_code as bs_code, per.employee_no as emp_no, emp.employee_name as emp_name, per.months_years as mon_yr, per.performance_date as per_date, per.holiday_class as ho_cl, per.shift_class as shi_cl , " +
    				 "per.remarks as rem, per.performance_status as per_st, per.approval_employee_no as app_emp_no, per.approval_date as app_date, appemp.position_code as app_emp_position, appemp.employee_name as app_emp_name, " +
    				 "yma.months_years_status as mon_yr_st " +
    				 "from tbl_performance per inner join ms_employee emp join tbl_year_month_attribute yma on per.employee_no = emp.employee_no and per.employee_no = yma.employee_no " +
    				 "left outer join ( select employee_no, employee_name, position_code from ms_employee ) appemp on per.approval_employee_no = appemp.employee_no " +
    				 "where yma.months_years_status in ('02', '03') and per.performance_status != :status and per.months_years = :yearmonth and yma.business_team_code in (:btc) " +
    				 "group by per_date  order by emp_no , per_date";
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
    	String sql = "update tbl_performance set performance_status = :perStatus, approval_employee_no = :appEmp, approval_date = Now() " +
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
    	String sql = "update tbl_performance set performance_status = :perStatus " +
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
