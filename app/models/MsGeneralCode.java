package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;

import common.Const;
import play.Logger;

/**
 * 汎用コード管理マスタ
 */
@Entity
public class MsGeneralCode extends CommonModel {

    /**
     * コード種別
     */
    @NotNull
    public String codeType;

    /**
     * 年度
     */
    @NotNull
    public String targetYear;

    /**
     * コード
     */
    @NotNull
    public String code;

    /**
     * コード名称
     */
    public String codeName;

    /**
     * 任意値1
     */
    public String anyValue1;

    /**
     * 任意値2
     */
    public String anyValue2;

    /**
     * 任意値3
     */
    public String anyValue3;

    /**
     * 任意値4
     */
    public String anyValue4;

    /**
     * 任意値5
     */
    public String anyValue5;

    /**
     * 取得対象コード種別（CODE_TYPE）、年度（YEAR)、コード（code）を指定して汎用コードを取得
     *
     * @param codeType コード種別
     * @param code コード
     * @param year 年度
     * @param anyValue1 任意値1
     */
    public static SqlRow getCodeMaster(String codeType, String code, String year, String anyValue1) {
        String sql = "SELECT * FROM MS_GENERAL_CODE " +
                     "WHERE CODE_TYPE = :codeType AND CODE = :code AND TARGET_YEAR = :year";

        List<SqlRow> sqlRows = new ArrayList<SqlRow>();
        if (anyValue1 != null && !anyValue1.isEmpty()) {
            sql = sql + " AND ANY_VALUE1 = :anyValue1";
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("codeType",codeType)
                    .setParameter("code",code)
                    .setParameter("year",year)
                    .setParameter("anyValue1", anyValue1)
                    .findList();
        } else {
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("codeType",codeType)
                    .setParameter("code",code)
                    .setParameter("year",year)
                    .findList();
        }

        SqlRow row = null;
        for (SqlRow o : sqlRows) {
            row = o;
        }
        return row;
    }

    /**
     * 取得対象コード（CODE）を指定して汎用コードを取得
     * 年度は固定で"0000"を指定
     *
     * @param codeType コードタイプ
     * @param code コード
     */
    public static SqlRow getCodeMaster(String codeType, String code) {
        return getCodeMaster(codeType, code, "0000", null);
    }

    /**
     * 取得対象コード種別（CODE_TYPE）、年度（YEAR)を指定して汎用コードリストを取得
     *
     * @param codeType コード種別
     * @param year 年度
     * @param anyValue1 任意値1
     */
    public static List<SqlRow> getCodeMasterList(String codeType, String year, String anyValue1) {
        String sql = "SELECT * FROM MS_GENERAL_CODE " +
                     "WHERE CODE_TYPE = :codeType AND TARGET_YEAR = :year";

        List<SqlRow> sqlRows = new ArrayList<SqlRow>();
        if (anyValue1 != null && !anyValue1.isEmpty()) {
            sql = sql + " AND ANY_VALUE1 = :anyValue1";
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("codeType",codeType)
                    .setParameter("year",year)
                    .setParameter("anyValue1", anyValue1)
                    .findList();
        } else {
            sqlRows = Ebean.createSqlQuery(sql)
                    .setParameter("codeType",codeType)
                    .setParameter("year",year)
                    .findList();
        }
        return sqlRows;
    }

    /**
     * 取得対象コード（CODE）を指定して汎用コードリストを取得
     * 年度は固定で"0000"を指定
     *
     * @param String コード
     */
    public static List<SqlRow> getCodeMasterList(String codeType) {
        return getCodeMasterList(codeType, "0000", null);
    }

    /**
     * 祝日マスタ取得（年月）
     *
     * @param year
     * @param month
     * @return sqlRows
     */
    public static List<SqlRow> getPublicHoliday(String year, String month) {
        String sql = "SELECT * FROM MS_GENERAL_CODE " +
                     "WHERE CODE_TYPE = 'PUBLIC_HOLIDAY_CLASS' AND TARGET_YEAR = :year AND ANY_VALUE1 = :month";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("year",year)
                .setParameter("month",month)
                .findList();
        return sqlRows;
    }

    /**
     * 祝日マスタ取得（年月日）
     *
     * @param year
     * @param month
     * @param date
     * @return sqlRows
     */
    public static boolean isPublicHoliday(String year, String month, String date) {
        String sql = "SELECT COUNT(*) cnt FROM MS_GENERAL_CODE " +
                     "WHERE CODE_TYPE = 'PUBLIC_HOLIDAY_CLASS' AND TARGET_YEAR = :year " +
                     "AND ANY_VALUE1 = :month AND ANY_VALUE2 = :date";

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
                .setParameter("year",year)
                .setParameter("month",month)
                .setParameter("date",date)
                .findList();

        if (sqlRows.get(0).getInteger("cnt") >= 1) {
            return true;
        }
        return false;
    }

    /**
     * 休暇区分取得
     * codeに該当する休暇区分のコード名称を取得します。
     *
     * @param holidayClassCode コード
     * @return sqlRows
     */
    public static String getClassNameByCode(String type,String code) {

        // 休暇区分コード00：無しの場合は取得不要のためreturn
        if (Const.DEFAULT_CODE.equals(code)) {
            return "";
        }
        String sql = "SELECT CODE_NAME as code_name FROM MS_GENERAL_CODE " +
                     "WHERE CODE_TYPE = :type AND CODE = :code";

        String codeName = "";

        SqlRow sqlRow = Ebean.createSqlQuery(sql)
        		.setParameter("type",type)
                .setParameter("code",code)
                .findUnique();

        codeName = sqlRow.getString("code_name");
        return codeName;
    }

    /**
     * コードタイプから時間(any_value1)取得
     *
     * @param code コード
     * @return double 時間
     */
    public static double getAnyValue1ByCode(String code,String codeType) {
        String sql = "SELECT ANY_VALUE1 as any_value1 FROM MS_GENERAL_CODE " +
                     "WHERE CODE_TYPE = :codeType AND CODE = :code";

        double anyValue1 = 0.0;

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        		.setParameter("codeType", codeType)
                .setParameter("code", code)
                .findList();

        for (SqlRow o : sqlRows) {
        	anyValue1 = o.getInteger("any_value1");
        }
        return anyValue1;
    }

    /**
     * 取得対象コード種別（CODE_TYPE）、年度（YEAR)、任意値2(ANY_VALUE2)を指定して汎用コードリストを取得
     *
     * @param codeType コード種別
     * @param year 年度
     * @param anyValue2 任意値2
     */
    public static List<SqlRow> getCodeListByAnyValue2(String codeType,List<String> anyValue2) {

        String sql = "SELECT * FROM MS_GENERAL_CODE " +
                     "WHERE CODE_TYPE = :codeType AND ANY_VALUE2 in( :anyValue2)" ;

        List<SqlRow> sqlRows = Ebean.createSqlQuery(sql)
        			.setParameter("codeType",codeType)
        			.setParameter("anyValue2",anyValue2)
                    .findList();

        return sqlRows;
    }
}
