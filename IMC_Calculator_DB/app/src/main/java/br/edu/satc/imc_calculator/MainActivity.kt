package br.edu.satc.imc_calculator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import java.math.RoundingMode
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPref:SharedPreferences;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPref = getSharedPreferences("br.edu.satc.imc_calculator.IMC", Context.MODE_PRIVATE) ?: return

        //Carrega as informações na tela
        LoadScreen();

        val btnInformation = findViewById<Button>(R.id.btnInformation);
        btnInformation.setOnClickListener {
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)

            val lblResult = findViewById<TextView>(R.id.lblResult)
            lblResult.text = ""
            LoadScreen();

        }

        val btnCalculator = findViewById<Button>(R.id.btnCalculator);
        btnCalculator.setOnClickListener {

            val tbxWeight = findViewById<EditText>(R.id.tbxWeight)
            val tbxHeight = findViewById<EditText>(R.id.tbxHeight)
            val lblResult = findViewById<TextView>(R.id.lblResult)

            if (tbxWeight.text.toString() != "" && tbxHeight.text.toString() != "") {
                val imc = calcIMC(tbxWeight.text.toString(), tbxHeight.text.toString())
                val imcResult = "IMC: " + formatNumber(imc) + " - Estado: " + checkIMC(imc)
                lblResult.text = imcResult

                saveData(tbxWeight.text.toString(), tbxHeight.text.toString(), formatNumber(imc), checkIMC(imc));

            } else {
                lblResult.text = "Valores nulos."
            }
        }
    }

    private fun LoadScreen() {

        val weight = recoverWeight();
        val height = recoverHeight();
        val imc = recoverIMC();
        val state = recoverState();

        if(weight != null && weight != "" && height != null && height != "" && imc != null && imc != "" && state != null && state != "")
            findViewById<TextView>(R.id.lblHistory).text = "Peso: " + weight + " | Altura: " + height + " | IMC: " + imc + "\n Estado: " + state;
         else
            findViewById<TextView>(R.id.lblHistory).text = "Nenhum registro encontrado!";

    }

    private fun recoverWeight () : String? {
        return sharedPref.getString("vl_weight", null);
    }

    private fun recoverHeight() : String? {
        return sharedPref.getString("vl_height", null);
    }

    private fun recoverIMC() : String? {
        return sharedPref.getString("vl_imc", null);
    }

    private fun recoverState() : String? {
        return sharedPref.getString("vl_state", null);
    }

    private fun saveData(weight:String, height:String, imc:String, state:String) {

        with(sharedPref.edit()) {

            putString("vl_weight", weight);
            putString("vl_height", height);
            putString("vl_imc", imc);
            putString("vl_state", state);
            commit();

        }
    }

    // Cálculo do IMC
    private fun calcIMC(weight: String, height: String): Double =
        weight.toDouble() / (height.toDouble() * height.toDouble())

    // Retorna string de acordo com o cálculo
    private fun checkIMC(value: Double): String {

        if (value > 0 && value < 17.1)
            return "Muito abaixo do peso.";
        else if (value >= 17.1 && value < 18.5)
            return "Abaixo do peso.";
        else if (value >= 18.5 && value < 25)
            return "Peso normal.";
        else if (value >= 25 && value < 30)
            return "Acima do peso.";
        else if (value >= 30 && value < 35)
            return "Obesidade I.";
        else if (value >= 35 && value < 40)
            return "Obesidade II(severa).";
        else
            return "Obesidade III(mórbida).";
    }

    private fun formatNumber(random: Double): String {

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.DOWN
        val roundoff = df.format(random)
        return roundoff.toString()
    }
}