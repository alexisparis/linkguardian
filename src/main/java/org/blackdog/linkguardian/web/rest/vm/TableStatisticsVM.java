package org.blackdog.linkguardian.web.rest.vm;

public class TableStatisticsVM {

    private String tablename;

    private Long greatestPkValue;

    private String pkSequenceName;

    private Long pkSequenceNextValue;

    private Long itemCount;

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public Long getGreatestPkValue() {
        return greatestPkValue;
    }

    public void setGreatestPkValue(Long greatestPkValue) {
        this.greatestPkValue = greatestPkValue;
    }

    public String getPkSequenceName() {
        return pkSequenceName;
    }

    public void setPkSequenceName(String pkSequenceName) {
        this.pkSequenceName = pkSequenceName;
    }

    public Long getPkSequenceNextValue() {
        return pkSequenceNextValue;
    }

    public void setPkSequenceNextValue(Long pkSequenceNextValue) {
        this.pkSequenceNextValue = pkSequenceNextValue;
    }

    public Long getItemCount() {
        return itemCount;
    }

    public void setItemCount(Long itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return "TableStatisticsVM{" + "tablename='" + tablename + '\'' + ", greatestPkValue=" + greatestPkValue + ", pkSequenceName='" + pkSequenceName + '\''
            + ", pkSequenceNextValue=" + pkSequenceNextValue + ", itemCount=" + itemCount + '}';
    }
}
