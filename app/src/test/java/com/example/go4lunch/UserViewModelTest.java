package com.example.go4lunch;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.RepositoryUser;
import com.example.go4lunch.viewmodel.UserViewModel;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;
import java.util.Observer;

import bolts.Task;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class UserViewModelTest {
    private User currentUser = new User("abcd", "Naruto", "Uzumaki", null),
            secondUser = new User("drtv", "Sasuke", "Ushiwa", null),
            thirdUser = new User("qsdv", "Hatake", "Kakashi", null);
    private UserViewModel userViewModel;
    private MutableLiveData<List<User>> listMutableLiveData = new MutableLiveData<>();
    private Observer observer;
    private RepositoryUser repositoryUser;

    List<User> userList = Arrays.asList(
            currentUser,
            secondUser,
            thirdUser
    );

    /**@Before
    public void init(){
        listMutableLiveData.postValue(userList);
        repositoryUser = mock(RepositoryUser.class);
        when(repositoryUser.getUser("abcd")).thenReturn(currentUser);
    }*/


}
