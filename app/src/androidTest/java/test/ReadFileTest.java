package test;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.core.deps.guava.io.Resources;
import android.support.test.runner.AndroidJUnit4;

import com.example.gui.test.R;
import com.example.manager.HandleFiles;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;


/**
 * Created by jti on 2/26/18.
 */


@RunWith(AndroidJUnit4.class)


public class ReadFileTest{

    private List<String>  list = new ArrayList<String>();

    Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
    HandleFiles hf = new HandleFiles((ArrayList<String>) list,context);

  @Test
    public void readFile() throws IOException {
      ArrayList<String> list = new ArrayList<String>();

      list.add("moikka");

      hf.saveModifiedList(list);

      assertThat(hf.getList().get(0),is("moikka"));


  }









}
