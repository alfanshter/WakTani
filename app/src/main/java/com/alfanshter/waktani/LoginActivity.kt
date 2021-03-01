package com.alfanshter.waktani

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.alfanshter.waktani.session.SessionManager
import com.alfanshter.waktani.viewmodel.UserState
import com.alfanshter.waktani.viewmodel.UsersViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.*
import java.util.HashMap


@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {
    private lateinit var userViewModel : UsersViewModel
    lateinit var progressDialog: ProgressDialog
    lateinit var sessionManager: SessionManager
    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    var token : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sessionManager = SessionManager(this)
        sessionlogin()
        gettoken()
        progressDialog = ProgressDialog(this)
        userViewModel = ViewModelProviders.of(this).get(UsersViewModel::class.java)

        txt_daftar.setOnClickListener {
            startActivity<RegisterActivity>()
        }

        configureGoogleSignIn()
        setupUI()
        firebaseAuth = FirebaseAuth.getInstance()


    }

    private fun configureGoogleSignIn() {
        val mGoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI() {
        google_button.setOnClickListener {
            signIn()
        }
    }
    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                toast("Log In gagal")
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user_id = firebaseAuth.currentUser!!.uid
                sessionManager.setToken(user_id)
                userViewModel.getState().observer(this, Observer {
                    handleUIState(it)
                })
                userViewModel.getuseruid(user_id)

            } else {
                toast("Log In gagal")
            }
        }
    }


    private fun createdatafirestore(){
        val user_id = firebaseAuth.currentUser!!.uid
        val email = firebaseAuth.currentUser!!.email
        val name = firebaseAuth.currentUser!!.displayName
        val telefon = firebaseAuth.currentUser!!.phoneNumber
        val foto = firebaseAuth.currentUser!!.photoUrl
        val token = token.toString()

        userViewModel.newlogin(name.toString(),email.toString(),telefon.toString(),foto.toString(),user_id,token)
    }
    private fun handleUIState(it : UserState){
        when(it){
            is UserState.Error -> {
                isLoading(false)
                toast("${it.err}")
            }

            is UserState.Failed -> {
                isLoading(false)
                toast(it.message)
            }

            is  UserState.CekUid ->{
                if (it.cekuid){
                    sessionManager.setLogin(true)
                    startActivity(intentFor<HomeActivity>().clearTask().clearTop())
                    finish()
                }else{
                    createdatafirestore()
                }
            }

            is UserState.Success->{
                if (it.status){
                    sessionManager.setLogin(true)
                    startActivity(intentFor<HomeActivity>().clearTask().clearTop())
                    finish()
                }else{
                    toast("silahkan ulangi lagi")
                }
            }

            is UserState.IsLoading -> isLoading(it.state)
        }
    }


    private fun isLoading(state: Boolean) {
        if (state){
            progressDialog.dismiss()
        }else{
            progressDialog.show()
        }

    }

    private fun gettoken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                toast("gagal dapat token")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
        })
    }

    fun sessionlogin(){
        if (sessionManager.getLogin()==true){
            startActivity(intentFor<HomeActivity>().clearTask().clearTop())
            finish()
        }
    }







}