package uu.toolbox.data;

import android.content.Context;
import android.support.annotation.NonNull;


public class UUTestDatabase extends UUDefaultDatabase
{
    public UUTestDatabase(@NonNull final Context context)
    {
        super(context, new DbDef());
    }



    public static final String NAME = "uu_test_db";


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // UUDatabaseDefinition
    ////////////////////////////////////////////////////////////////////////////////////////////////

//    @Override
//    public int getVersion()
//    {
//        return CURRENT_VERSION;
//    }
//


    @UUSqlDatabase(name = NAME, models =
    {
        UUTestDataModel.class,
        UUDataModelWithCompoundKey.class,
        UUComplexDataModel.class,
        DataModelWithObjPrimitiveTypes.class
    })
    public static class DbDef implements UUDatabaseDefinition
    {
        public static final int VERSION_ONE = 1;
        public static final int VERSION_TWO = 2;
        public static final int VERSION_THREE = 3;
        public static final int VERSION_FOUR = 4;

        public static int CURRENT_VERSION = VERSION_FOUR;

        @Override
        public int getVersion()
        {
            return CURRENT_VERSION;
        }

        /*
        public String getDatabaseName()
        {
            return NAME;
        }

        public int getVersion()
        {
            return CURRENT_VERSION;
        }

        public ArrayList<UUDataModel> getDataModels(int version)
        {
            ArrayList<UUDataModel> list = new ArrayList<>();

            if (version >= VERSION_ONE)
            {
                list.add(new UUTestDataModel());
            }

            if (version >= VERSION_TWO)
            {
                list.add(new UUDataModelWithCompoundKey());
            }

            if (version >= VERSION_THREE)
            {
                list.add(new UUComplexDataModel());
            }

            if (version >= VERSION_FOUR)
            {
                list.add(new DataModelWithObjPrimitiveTypes());
            }

            return list;
        }*/
    }
}
