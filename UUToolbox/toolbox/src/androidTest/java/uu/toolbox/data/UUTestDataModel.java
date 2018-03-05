package uu.toolbox.data;

@UUSqlTable(tableName = "uu_test_model")
public class UUTestDataModel implements UUDataModel
{
    @UUSqlColumn(primaryKey = true)
    public int id;

    @UUSqlColumn()
    public String name;

    @UUSqlColumn(existsInVersion = 2)
    public int number;

    @UUSqlColumn()
    public String team;
}
