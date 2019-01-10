package models;

import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

import services.utils.CheckUtil;

/**
 * 年月属性テーブル
 */
@Entity
public class TblYearMonthAttribute extends CommonModel {

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
     * 部署コード
     */
    public String departmentCode;

    /**
     * 課コード
     */
    public String divisionCode;

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
     * 年月別ステータス
     */
    @NotNull
    public String monthsYearsStatus;

    /**
     * 年月属性データ取得
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRow
     */
    public static SqlRow getYearMonthData(String empNo, String yearMonth) {
        String sql = "SELECT * FROM MS_EMPLOYEE EMP INNER JOIN TBL_YEAR_MONTH_ATTRIBUTE MONTH ON EMP.EMPLOYEE_NO = MONTH.EMPLOYEE_NO " +
                     "WHERE EMP.EMPLOYEE_NO = :empNo AND MONTH.MONTHS_YEARS = :yearmonth";
        SqlRow row = null;

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("empNo", empNo)
                .setParameter("yearmonth", yearMonth)
                .findList();
        for(SqlRow o : sqlRows) {
            row = o;
        }
        return row;
    }

    /**
     * 年月属性データ更新
     * @param yearMonthData 年月属性テーブル
     */
    public static void updateYearMonthData(TblYearMonthAttribute yearMonthData) {
        String sql = "UPDATE TBL_YEAR_MONTH_ATTRIBUTE SET BUSINESS_CODE = :bus, BUSINESS_TEAM_CODE = :busteam,"
        		   + "DEPARTMENT_CODE = :dep," +
                     "DIVISION_CODE = :divi, MONTHS_YEARS_STATUS = :sta, BREAKDOWN_NAME1 = :break1," +
                     "BREAKDOWN_NAME2 = :break2, BREAKDOWN_NAME3 = :break3, BREAKDOWN_NAME4 = :break4 " +
                     "WHERE EMPLOYEE_NO = :empNo AND MONTHS_YEARS = :yearmonth";
        Ebean.beginTransaction();
        try {
        SqlUpdate create = Ebean.createSqlUpdate(sql)
                .setParameter("bus",yearMonthData.businessCode)
                .setParameter("busteam",yearMonthData.businessTeamCode)
                .setParameter("dep",yearMonthData.departmentCode)
                .setParameter("divi",yearMonthData.divisionCode)
                .setParameter("sta",yearMonthData.monthsYearsStatus)
                .setParameter("break1",yearMonthData.breakdownName1)
                .setParameter("break2",yearMonthData.breakdownName2)
                .setParameter("break3",yearMonthData.breakdownName3)
                .setParameter("break4",yearMonthData.breakdownName4)
                .setParameter("empNo",yearMonthData.employeeNo)
                .setParameter("yearmonth",yearMonthData.monthsYears);
            Ebean.execute(create);
            Ebean.commitTransaction();
        } catch (Exception e) {
            Ebean.rollbackTransaction();
            // FIXME debug
            System.out.println(CheckUtil.getClassName()+ " " +e);
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * 年月属性データ登録
     * @param yearMonthData 年月属性テーブル
     */
    public static void insertYearMonthData(TblYearMonthAttribute yearMonthData) throws Exception {
        Ebean.beginTransaction();
        try {
            yearMonthData.insert();
            Ebean.commitTransaction();
        } catch (Exception e) {
            // FIXME debug
            System.out.println(CheckUtil.getClassName()+ " " +e);
            Ebean.rollbackTransaction();
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }

    /**
     * 年月属性データのステータス更新
     * @param yearMonthData 年月属性テーブル
     */
    public static void updateYearMonthDataStatus(String employeeNo,String monthsYears,String monthsYearsStatus) {
        String sql = "UPDATE TBL_YEAR_MONTH_ATTRIBUTE SET " +
                     "MONTHS_YEARS_STATUS = :sta " +
                     "WHERE EMPLOYEE_NO = :empNo AND MONTHS_YEARS = :yearmonth";
        Ebean.beginTransaction();
        try {
        SqlUpdate create = Ebean.createSqlUpdate(sql)
                .setParameter("empNo",employeeNo)
                .setParameter("yearmonth",monthsYears)
                .setParameter("sta",monthsYearsStatus);
            Ebean.execute(create);
            Ebean.commitTransaction();
        } catch (Exception e) {
            Ebean.rollbackTransaction();
            // FIXME debug
            System.out.println(CheckUtil.getClassName()+ " " +e);
            throw e;
        } finally {
            Ebean.endTransaction();
        }
    }
}
