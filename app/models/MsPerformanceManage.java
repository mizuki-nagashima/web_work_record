package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.omg.CORBA.Current;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;

import models.form.ApproveFormList;
import play.Logger;
import services.utils.DateUtil;

/**
 * 実績テーブル
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
     * 業務チームコード取得
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRows
     */
    public static List<SqlRow> getBusinessTeamCode(String empNo) {
        String sql = "select * from ms_performance_manage " +
                     "where employee_no = :emp and start_date <= current_timestamp";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        		.setParameter("emp" ,empNo)
                .findList();

        Logger.debug("sql:" + String.valueOf(sql));

        return sqlRows;
    }

    /**
     * 承認画面年月リスト取得SQL
     * @param empNo 社員番号
     * @param yearMonth 年月
     * @return sqlRows
     */
    public static List<SqlRow> getApproveYearMonth(String emp) {
        String sql = "select min(start_date) from ms_performance_manage " +
                     "where employee_no = :empNo";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        		.setParameter("empNo", emp)
                .findList();

        Logger.debug("sql:" + String.valueOf(sql));

        return sqlRows;
    }

}
