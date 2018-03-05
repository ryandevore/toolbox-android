package uu.toolbox.data;

@UUSqlTable(tableName = "uu_test_model")
public class UUTestDataModel implements UUDataModel
{
    @UUSqlColumn(name = "id", type = UUSqlColumn.Type.INTEGER, primaryKey = true)
    public int id;

    @UUSqlColumn(name = "name", type = UUSqlColumn.Type.TEXT)
    public String name;

    @UUSqlColumn(name = "number", type = UUSqlColumn.Type.INTEGER, existsInVersion = 2)
    public int number;

    @UUSqlColumn(name = "team", type = UUSqlColumn.Type.TEXT)
    public String team;
}
