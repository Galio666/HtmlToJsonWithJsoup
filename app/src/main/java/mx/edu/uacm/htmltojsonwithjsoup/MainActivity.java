package mx.edu.uacm.htmltojsonwithjsoup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    TextView text;
    TextView texMatricula;
    TextView textCarrera;
    TextView textPlantel;
    TextView textTurno;
    TextView textPost;
    Elements contenido;
    Elements imagen;
    String imgSrc="";
    String Nombre;

    String Matricula;
    String Carrera;
    String Plantel;
    String Turno;
    String Situacion;
    String fecha;
    private postService postsService;



    public SignupRequest usuario;
    private static final String TAG = "Main aCtivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        text = (TextView) findViewById(R.id.txtNombre);
        texMatricula=(TextView) findViewById(R.id.txtMatricula);
        textCarrera=(TextView) findViewById(R.id.txtCarrera);
        textPlantel=(TextView)findViewById(R.id.txtPlantel);
        textTurno=(TextView)findViewById(R.id.txtTurno);
        textPost=(TextView) findViewById(R.id.txtPost);
        postRequest();
        getHtml();


        //System.out.println(converToPojo(getHtml()).toString());
        llenarCampos();




    }

    private String getHtml() {
        try {
            Document doc = Jsoup.connect("https://registroescolar.uacm.edu.mx/app/inicio/index/consultarInformacion?datos=BpwilWISAZI2fst69H2bh5TKCO32aliJpUHIDQZUoeE=s").get();
            //contenido = doc.select("td").;
            contenido=doc.select("table[class=table table-condensed table-responsive table-user-information]");
            imagen=doc.select("div[class=col-xs-12 col-md-3] img[src]");
            imgSrc=imagen.attr("src");
            //text.setText(contenido.attr());




            Elements rows = contenido.get(0).select("tr");

            for (Element row : rows) {

                if (row.select("td").size() == 7) {
                     Nombre = row.select("td").get(0).text();
                     Matricula = row.select("td").get(1).text();
                     Carrera = row.select("td").get(2).text();
                     Plantel = row.select("td").get(3).text();
                     Turno = row.select("td").get(4).text();
                     Situacion = row.select("td").get(5).text();
                     fecha = row.select("td").get(6).text();
                }



            }

            //String [] nombreseparado=Nombre.split(": ");
            System.out.println("---------------"+Nombre);

        } catch (IOException e) {

            Toast.makeText(this, "Failed to load HTML code",
                    Toast.LENGTH_SHORT).show();
        }
        return contenido.text()+" Imagen: "+imgSrc;

    }

    SignupRequest converToPojo(String cadena){
        SignupRequest pojo = new SignupRequest();
        ArrayList datosPojo = new ArrayList();
        String exp = "Nombre|Matrícula|Carrera|Plantel|Turno|Situación alumno|Fecha de emisión|Imagen";
        String[] palabras = cadena.split(":");
        for (String palabra : palabras) {
            String palabraSin = palabra.replaceAll(exp, "");
            datosPojo.add(palabraSin);
        }
        pojo.setNombre((String) datosPojo.get(1));
        pojo.setMatricula((String) datosPojo.get(2));
        pojo.setCarrera((String) datosPojo.get(3));
        pojo.setPlantel((String) datosPojo.get(4));
        pojo.setTurno((String) datosPojo.get(5));
        pojo.setUrlfoto(imgSrc);

        return pojo;
    }

    public void llenarCampos(){

        text.setText(converToPojo(getHtml()).getNombre());
        texMatricula.setText(converToPojo(getHtml()).getMatricula());
        textCarrera.setText(converToPojo(getHtml()).getCarrera());
        textPlantel.setText(converToPojo(getHtml()).getPlantel());
        textTurno.setText(converToPojo(getHtml()).getTurno());
    }

    public void postRequest(){

        try {


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://incidencias-servicio-backend.herokuapp.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            postsService = retrofit.create(postService.class);
            sendPost();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void sendPost() {
        SignupRequest post = new SignupRequest();
        post.setEmail(converToPojo(getHtml()).getNombre());
        post.setNombre(converToPojo(getHtml()).getNombre());
        post.setMatricula(converToPojo(getHtml()).getMatricula());
        post.setCarrera(converToPojo(getHtml()).getCarrera());
        post.setPlantel(converToPojo(getHtml()).getPlantel());
        post.setUrlfoto(imgSrc);
        post.setPassword("");
        post.setUsername("");
        post.setRole(Collections.singleton(""));



        Call<SignupRequest> call = postsService.sendPosts(post);

        call.enqueue(new Callback<SignupRequest>() {


            @Override
            public void onResponse(Call<SignupRequest> call, Response<SignupRequest> response) {
                textPost.setText(response.body().toString());
            }

            @Override
            public void onFailure(Call<SignupRequest> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(), Toast.LENGTH_LONG).show();
            }

        });
    }


}

