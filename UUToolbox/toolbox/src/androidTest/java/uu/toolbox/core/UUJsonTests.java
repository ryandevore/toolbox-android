package uu.toolbox.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UUJsonTests
{
    private static void assertEquals(@Nullable final BaseballPlayer lhs, @Nullable final BaseballPlayer rhs)
    {
        Assert.assertNotNull(lhs);
        Assert.assertNotNull(rhs);
        Assert.assertEquals(lhs.position, rhs.position);
        Assert.assertEquals(lhs.number, rhs.number);
        Assert.assertEquals(lhs.name, rhs.name);
        Assert.assertEquals(lhs.birthday, rhs.birthday);
        Assert.assertEquals(lhs.average, rhs.average);
        Assert.assertEquals(lhs.isActive, rhs.isActive);
    }

    private static BaseballPlayer randomPlayer()
    {
        BaseballPlayer p = new BaseballPlayer();
        p.position = Position.CenterField;
        p.number = 15;
        p.name = "Code Monkey";
        p.birthday = System.currentTimeMillis();
        p.average = 0.357f;
        p.isActive = true;
        return p;
    }

    @Test
    public void test_0000_testToFromJson() throws Exception
    {
        BaseballPlayer p = randomPlayer();
        JSONObject obj = p.toJsonObject();

        BaseballPlayer deserialized = UUJson.parseJsonObject(BaseballPlayer.class, obj);
        assertEquals(p, deserialized);
    }
}

enum Position
{
    Pitcher,
    Catcher,
    FirstBase,
    SecondBase,
    ThirdBase,
    ShortStop,
    LeftField,
    CenterField,
    RightField
}

class BaseballPlayer implements UUJsonConvertible
{
    private enum JsonKeys
    {
        Position,
        Number,
        Name,
        Birthday,
        Average,
        IsActive
    }

    Position position;
    int number;
    String name;
    long birthday;
    double average;
    boolean isActive;

    @NonNull
    @Override
    public JSONObject toJsonObject()
    {
        JSONObject json = new JSONObject();

        UUJson.safePut(json, JsonKeys.Position, position);
        UUJson.safePut(json, JsonKeys.Number, number);
        UUJson.safePut(json, JsonKeys.Name, name);
        UUJson.safePut(json, JsonKeys.Birthday, birthday);
        UUJson.safePut(json, JsonKeys.Average, average);
        UUJson.safePut(json, JsonKeys.IsActive, isActive);


        return json;
    }

    @Override
    public void fillFromJson(@NonNull JSONObject json)
    {
        position = UUJson.safeGetEnum(Position.class, json, JsonKeys.Position);
        number = UUJson.safeGetInt(json, JsonKeys.Number);
        name = UUJson.safeGetString(json, JsonKeys.Name);
        birthday = UUJson.safeGetLong(json, JsonKeys.Birthday);
        average = UUJson.safeGetDouble(json, JsonKeys.Average);
        isActive = UUJson.safeGetBool(json, JsonKeys.IsActive);
    }
}

