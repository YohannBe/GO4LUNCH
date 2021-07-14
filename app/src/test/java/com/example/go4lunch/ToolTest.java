package com.example.go4lunch;

import android.location.Location;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.User;
import com.example.go4lunch.tool.Tool;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(JUnit4.class)
public class ToolTest {
    private Tool tool;
    private Location location;
    String favorite;
    private User currentUser, secondUser;
    Lunch lunch ;
    HashMap<String, Lunch> hashMap ;

    @Before
    public void init(){
        tool = mock(Tool.class);
        location = mock(Location.class);
        currentUser = new User("abcd", "Naruto", "Uzumaki", null);
        secondUser = new User("drtv", "Sasuke", "Ushiwa", null);
        lunch = new Lunch("restaurantId", "abcd", "restaurantName", "restaurantType", "address");
        favorite = "restaurantId/restaurantName";
        ArrayList<String> favoriteList = new ArrayList<>();
        favoriteList.add(favorite);
        hashMap= new HashMap<>();
        String date = tool.giveDependingDate();
        hashMap.put(date, lunch);
        currentUser.setDateLunch(hashMap);
        currentUser.setFavorite(favoriteList);

    }


    @Test
    public void toUpperCase() {
        String check = "naruto";
        String result = tool.nameToUpperCase(check);
        assertEquals("Naruto", result);
    }

    @Test
    public void iDAzComparator(){
        String first = "azzrfezrzf";
        String second = "gdrtgvsqefrv";
        String expected = "azzrfezrzfgdrtgvsqefrv";
        String firstResult = tool.updateListUserSort(first, second);
        String secondResult = tool.updateListUserSort(second, first);

        assertEquals(expected, firstResult);
        assertEquals(expected, secondResult);
    }

    @Test
    public void checkDateExist(){
        assertTrue(tool.checkIfDateExist(currentUser));
    }

    @Test
    public void checkActualDate(){
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String result = tool.giveActualDate();
        assertEquals(date, result);
    }

    @Test
    public void checkIdRestaurant(){

        assertTrue(tool.checkIdDateRestaurantExist(currentUser, "restaurantId"));
        assertFalse(tool.checkIdDateRestaurantExist(currentUser, "restaurant"));
    }

    @Test
    public void checkNumberPersonRestaurant(){
        List<User> userList = new ArrayList<>();
        userList.add(currentUser);
        userList.add(secondUser);

        assertEquals(1, tool.checkExistingLunch(userList, "restaurantId"));
    }

    @Test
    public void checkFavoriteList(){
        assertTrue(tool.checkFavorite(currentUser.getFavorite(), "restaurantId/restaurantName"));
        assertFalse(tool.checkFavorite(currentUser.getFavorite(), "restaurant/restaurantName"));
    }

}
