package com.cenfotec.storage.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.cenfotec.storage.bd.DatabaseHelper;
import com.cenfotec.storage.bd.Usuario;
import com.cenfotec.storage.helpers.PreferencesManager;
import com.cenfotec.storage.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText usuario;
    EditText password;
    CheckBox remember;
    Button loginButton;
    Button registerButton;

    DatabaseHelper bdHelper;

    private static final int PERM_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = (EditText)findViewById(R.id.user_input);
        password = (EditText)findViewById(R.id.password_input);
        remember = (CheckBox) findViewById(R.id.remember);
        loginButton = (Button)findViewById(R.id.login);
        registerButton = (Button)findViewById(R.id.register);

        String usuarioStr = PreferencesManager.getUsernameFromPreferences(this);
        usuario.setText(usuarioStr);

        if(PreferencesManager.getRememberFromPreferences(this)) {
            remember.setChecked(true);
            String passwordStr = PreferencesManager.getPasswordFromPreferences(this);
            password.setText(passwordStr);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    PreferencesManager.savePreferences(MainActivity.this,
                            usuario.getText().toString(),
                            password.getText().toString(),
                            remember.isChecked());

                    //Inicializamos el BD Helper solo si hace falta
                    if (bdHelper == null) {
                        bdHelper = new DatabaseHelper(MainActivity.this);
                    }

                    //Obtenemos el nombre de usuario ingresado en el campo de texto
                    String usuarioIngresado = usuario.getText().toString().trim();

                    //Obtenemos el dao de la tabla de usuarios
                    Dao<Usuario, Integer> userDao = bdHelper.getUserDao();

                    //Generamos un filtro y obtenemos la lista resultado
                    Where filtro = userDao.queryBuilder()
                            .where()
                            .eq("username", usuarioIngresado);

                    List<Usuario> usuarios = filtro.query();

                    //Si no se encontro ningun usuario, es porque no existe
                    if(usuarios.size() == 0){
                        Toast.makeText(MainActivity.this, "Ese usuario no existe!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Obtenemos la referencia al usuario
                    Usuario user = usuarios.get(0);

                    String passwordIngresado = password.getText().toString();

                    //Si los passwords son diferentes, mostramos un error
                    if(!user.password.equals(passwordIngresado)){
                        Toast.makeText(MainActivity.this, "Password incorrecto!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //ENTRAR A LA CUENTA
                    Toast.makeText(MainActivity.this, "Bienvenido.", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e){
                    Log.d("Error", "Error");
                }
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Este método realmente no ocupa el permiso de escritura a almacenamiento externo,
                //sin embargo se presenta acá a modo de ejemplo.
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permissionCheck == PackageManager.PERMISSION_GRANTED){
                    //Si tenemos permiso, continuamos
                    continuar();
                } else {
                    //Si no, pedimos permiso
                    askForPermission();
                }
            }
        });
    }

    private void continuar(){
        Toast.makeText(this, "Tenemos Permiso!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, AddUserActivity.class);
        startActivity(intent);
    }

    private void askForPermission() {
        //Se solicita permiso. Esta llamada es asincronica, por lo que tenemos que
        //implementar el metodo callback onRequestPermissionResult para procesar la
        //respuesta del usuario (ver abajo)
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERM_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Si recibimos al menos un permiso y su valor es igual a PERMISSION_GRANTED, tenemos permiso
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
            registerButton.callOnClick();
        } else {
            Toast.makeText(this, ":(", Toast.LENGTH_SHORT).show();
        }
    }
}
