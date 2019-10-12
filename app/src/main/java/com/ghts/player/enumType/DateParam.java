package com.ghts.player.enumType;

/**
 * 表示日期显示格式的数据类型
 */
public class DateParam {


    // <Date_Param Show_Seq="0" Year_Show="4" MD_Show="1"
    // Year_Tip="年" Month_Tip="月" Day_Tip="日" />
    private DateShow show_seq;// 年月日的显示顺序。

    private YearShow year_show;// 年的显示方式。

    private MDShow MD_Show; // 月日的显示

    private String year_tip;// 年的表示

    private String month_tip;// 月的表示

    private String day_tip;// 日的表示

    public DateShow getShow_seq() {
        return show_seq;
    }

    public void setShow_seq(DateShow show_seq) {
        this.show_seq = show_seq;
    }

    public YearShow getYear_show() {
        return year_show;
    }

    public void setYear_show(YearShow year_show) {
        this.year_show = year_show;
    }

    public MDShow getMD_Show() {
        return MD_Show;
    }

    public void setMD_Show(MDShow mD_Show) {
        MD_Show = mD_Show;
    }

    public String getYear_tip() {
        return year_tip;
    }

    public void setYear_tip(String year_tip) {
        this.year_tip = year_tip;
    }

    public String getMonth_tip() {
        return month_tip;
    }

    public void setMonth_tip(String month_tip) {
        this.month_tip = month_tip;
    }

    public String getDay_tip() {
        return day_tip;
    }

    public void setDay_tip(String day_tip) {
        this.day_tip = day_tip;
    }


}
