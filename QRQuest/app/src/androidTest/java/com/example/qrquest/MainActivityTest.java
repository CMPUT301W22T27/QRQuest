package com.example.qrquest;


import android.app.Activity;

import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance. *
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Acticity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }
    @Test
    public void checkMainActivity(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
    @Test
    public void checkCreateAccountActivity(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.createNewAccountButton));
        solo.assertCurrentActivity("Wrong Activity",CreateAccount.class);
    }
    @Test
    public void checkMainScreen(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.createNewAccountButton));
        solo.assertCurrentActivity("Wrong Activity",CreateAccount.class);
        solo.enterText((EditText) solo.getView(R.id.editTextTextUserName), "Jay");
        solo.enterText((EditText) solo.getView(R.id.editTextTextEmailAddress), "testEmail@gmail.com");
        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity", MainScreen.class);
    }
    @Test
    public void checkMainScreenActivity(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.createNewAccountButton));
        solo.assertCurrentActivity("Wrong Activity",CreateAccount.class);
        solo.enterText((EditText) solo.getView(R.id.editTextTextUserName), "Jay");
        solo.enterText((EditText) solo.getView(R.id.editTextTextEmailAddress), "testEmail@gmail.com");
        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.viewMapButton));
        solo.assertCurrentActivity("Wrong Activity", MainScreenActivity.class);
    }

    @Test
    public void checkGenerateLogInQRCode(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.createNewAccountButton));
        solo.assertCurrentActivity("Wrong Activity",CreateAccount.class);
        solo.enterText((EditText) solo.getView(R.id.editTextTextUserName), "Jay");
        solo.enterText((EditText) solo.getView(R.id.editTextTextEmailAddress), "testEmail@gmail.com");
        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.generateQRCodeButton));
        solo.assertCurrentActivity("Wrong Activity", ChooseQRCodeType.class);
        solo.clickOnButton("Login Code");
        solo.assertCurrentActivity("Wrong ACtivity", LoginQRCode.class);
    }

    @Test
    public void checkGenerateGameStatusCode(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.createNewAccountButton));
        solo.assertCurrentActivity("Wrong Activity",CreateAccount.class);
        solo.enterText((EditText) solo.getView(R.id.editTextTextUserName), "Jay");
        solo.enterText((EditText) solo.getView(R.id.editTextTextEmailAddress), "testEmail@gmail.com");
        solo.clickOnButton("Confirm");
        solo.assertCurrentActivity("Wrong Activity", MainScreen.class);
        solo.clickOnView(solo.getView(R.id.generateQRCodeButton));
        solo.assertCurrentActivity("Wrong Activity", ChooseQRCodeType.class);
        solo.clickOnButton("Game Status Code");
        solo.assertCurrentActivity("Wrong ACtivity", GameStatusQRCode.class);
    }
}
