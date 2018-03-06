package uu.toolbox.data;

@UUSqlTable(tableName = "uu_data_model_with_compound_key", existsInVersion = 2)
public class UUDataModelWithCompoundKey implements UUDataModel
{
    @UUSqlColumn(name = "c1", primaryKey = true)
    public String c1;

    @UUSqlColumn(name = "c2", primaryKey = true)
    public String c2;

    @UUSqlColumn(name = "data")
    public String data;
}
