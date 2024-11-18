package com.example.devems

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var enterCodeEditText: EditText
    private lateinit var idEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var signupButton: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        enterCodeEditText = findViewById(R.id.enterCodeEditText)
        idEditText = findViewById(R.id.idEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        signupButton = findViewById(R.id.signupButton)

        signupButton.setOnClickListener { handleSignup() }
    }

    private fun handleSignup() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()
        val enterCode = enterCodeEditText.text.toString().trim()
        val idText = idEditText.text.toString().trim()
        val phoneNumber = phoneNumberEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || idText.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        val id = idText.toIntOrNull()
        if (id == null) {
            Toast.makeText(this, "ID must be a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        if (enterCode.isEmpty() || enterCode != "4444") {
            Toast.makeText(this, "Invalid admin code", Toast.LENGTH_SHORT).show()
            return
        }

        val dbHelper = UserDatabaseHelper(this)
        if (dbHelper.isIdExists(id)) {
            Toast.makeText(this, "ID already exists", Toast.LENGTH_SHORT).show()
            return
        }

        val isAdded = dbHelper.addUser(id, username, password, phoneNumber)
        if (isAdded) {
            Toast.makeText(this, "Signup successful! Please login.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Signup failed, please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}
