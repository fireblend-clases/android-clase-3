package com.cenfotec.storage.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cenfotec.storage.R;
import com.cenfotec.storage.bd.DatabaseHelper;
import com.cenfotec.storage.bd.Usuario;
import com.j256.ormlite.dao.Dao;

import java.util.List;

/**
 * Created by Estudiantes on 24/08/2017.
 */

public class AddUserActivity extends AppCompatActivity {

    EditText name;
    EditText usernameTxt;
    EditText passwordTxt;
    EditText password2Txt;
    EditText edadTxt;
    Button boton;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        name = (EditText) findViewById(R.id.name);
        usernameTxt = (EditText) findViewById(R.id.username);
        passwordTxt = (EditText) findViewById(R.id.password);
        password2Txt = (EditText) findViewById(R.id.password_2);
        edadTxt = (EditText) findViewById(R.id.edad);
        boton = (Button) findViewById(R.id.button);


        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String strName = name.getText().toString().trim();
                    String username = usernameTxt.getText().toString().trim();
                    String password = passwordTxt.getText().toString();
                    String password2 = password2Txt.getText().toString();

                    int edad = Integer.parseInt(edadTxt.getText().toString().trim());

                    //Inicializamos el DBHelper
                    if(dbHelper == null) {
                        dbHelper = new DatabaseHelper(AddUserActivity.this);
                    }

                    //Recuperamos el dao
                    Dao<Usuario, Integer> userDao = dbHelper.getUserDao();

                    //Recuperamos todos los usuarios que tengan ese mismo username
                    List<Usuario> usuarios = dbHelper.getUserDao().queryBuilder().
                            where().eq("username", username.trim()).query();

                    if(!password.equals(password2)){
                        //MOSTRAR ERROR
                        Toast.makeText(AddUserActivity.this, "Passwords no concuerdan.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(usuarios.size() > 0){
                        Toast.makeText(AddUserActivity.this, "Ese usuario ya existe.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Usuario nuevo = new Usuario();
                    nuevo.edad = edad;
                    nuevo.username = username;
                    nuevo.password = password;
                    nuevo.nombre = strName;

                    userDao.createOrUpdate(nuevo);
                    finish();
                } catch (Exception e){
                    Toast.makeText(AddUserActivity.this, "Error creando cuenta.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}