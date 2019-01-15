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
 * 実績管理用テーブル
 */
@Entity
public class TblPerformanceAdmin extends CommonModel {

    /**
     * 社員番号
     */
    @NotNull
    public String employeeNo;

    /**
     * 年
     */
    @NotNull
    public String year;

    /**
     * 月
     */
    @NotNull
    public String month;

    /**
     * 実績時間
     */
    public Double sumPerformanceTime;


    /**
     * 控除（深夜）
     */
    public Double sumDeductionNight;

    /**
     * 控除（その他）
     */
    public Double sumDeductionOther;

    /**
     * 有給割当合計
     */
    public Double sumSalaried;

    /**
     * 超過時間
     */
    public Double overtime;

    /**
     * 出勤日数
     */
    public String attendanceDay;

    /**
     * 欠勤日数
     */
    public String absenceDay;

    /**
     * 休日出勤日数
     */
    public String attendanceHoliday;

    /**
     * 代休日数
     */
    public String compHoliday;

    /**
     * 有給日数
     */
    public String salariedHoliday;

    /**
     * 夏季休暇日数
     */
    public String summerHoliday;

    /**
     * 年末年始休暇日数
     */
    public String newyearHoliday;

    /**
     * 特別休暇日数
     */
    public String supecialHoliday;

    /**
     * 看護休暇日数
     */
    public String nursingLeave;

    /**
     * 承認者社員番号
     */
    public String approvalEmployeeNo;

    /**
     * 承認日時
     */
    public Timestamp approvalDate;

    /**
     * 実績管理用データ取得
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRows
     */
    public static List<SqlRow> getPerformanceData() {
        String sql = "SELECT DISTINCT  TB.EMPLOYEE_NO,EM.EMPLOYEE_NAME,EM.POSITION_CODE,EM.BUSINESS_CODE AS BUSINESS_CODE,EM2.EMPLOYEE_NAME AS LEADER " +
        		"FROM TBL_PERFORMANCE_ADMIN TB " +
        		"LEFT JOIN MS_EMPLOYEE EM ON TB.EMPLOYEE_NO=EM.EMPLOYEE_NO " +
        		"LEFT JOIN (" +
        		"SELECT MS.EMPLOYEE_NO, MS.EMPLOYEE_NAME,MS.POSITION_CODE,PM.BUSINESS_CODE,PM.BUSINESS_TEAM_CODE  " +
        		"FROM KINTAI.MS_EMPLOYEE MS  " +
        		"LEFT OUTER JOIN MS_PERFORMANCE_MANAGE PM ON PM.EMPLOYEE_NO= MS.EMPLOYEE_NO " +
        		"WHERE MS.POSITION_CODE != :code) EM2 " +
        		"ON EM.BUSINESS_CODE=EM2.BUSINESS_CODE ";
        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        		.setParameter("code", Const.DEFAULT_CODE)
                .findList();

        return sqlRows;
    }

    /**
     * 実績管理用データ登録
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

}
