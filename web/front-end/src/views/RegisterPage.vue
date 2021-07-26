<template>
  <div class="container public">
    <div class="row justify-content-center" style="margin-left: 17%;width: 60%;background-color: RGB(241, 196, 15);border: 1px solid black;margin-top:100px;">
      <div class="form">
        <Logo/>
        <div class="tagline text-center">File Management System</div>
        <br>
        <form @submit.prevent="submitForm">
          <div v-show="errorMessage" class="alert alert-danger failed">{{ errorMessage }}</div>
          <div class="form-group">
            <label for="username">Username</label>
            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><font-awesome-icon icon="user" size="lg"/></span>
              </div>
              <input type="text" class="form-control" id="username" v-model="form.username">
            </div>
              <div class="field-error" v-if="$v.form.username.$dirty">
              <div class="login-error" v-if="!$v.form.username.required">Username is required</div>
              <div class="login-error" v-if="!$v.form.username.alphaNum">Username can only contain letters and numbers</div>
              <div class="login-error" v-if="!$v.form.username.minLength">Username must have at least {{ $v.form.username.$params.minLength.min }} letters.</div>
              <div class="login-error" v-if="!$v.form.username.maxLength">Username is too long. It can contains maximium {{ $v.form.username.$params.maxLength.max }} letters.</div>
              </div>
          </div>
          <div class="form-group">
            <label for="emailAddress">Email address</label>
            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><font-awesome-icon icon="envelope" size="lg"/></span>
              </div>
            <input type="email" class="form-control" id="emailAddress" v-model="form.emailAddress">
            </div>
            <div class="field-error" v-if="$v.form.emailAddress.$dirty">
              <div class="login-error" v-if="!$v.form.emailAddress.required">Email address is required</div>
              <div class="login-error" v-if="!$v.form.emailAddress.email">This is not a valid email address</div>
              <div class="login-error" v-if="!$v.form.emailAddress.maxLength">Email address is too long. It can contains maximium {{ $v.form.emailAddress.$params.maxLength.max }} letters.</div>
            </div>
          </div>
          <div class="form-group">
            <label for="password">Password</label>
            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><font-awesome-icon icon="key" size="lg"/></span>
              </div>
              <input type="password" class="form-control" id="password" v-model="form.password">
            </div>
            <div class="field-error" v-if="$v.form.password.$dirty">
              <div class="login-error" v-if="!$v.form.password.required">Password is required</div>
              <div class="login-error" v-if="!$v.form.password.minLength">Password is too short. It can contains at least {{ $v.form.password.$params.minLength.min }} letters.</div>
              <div class="login-error" v-if="!$v.form.password.maxLength">Password is too long. It can contains maximium {{ $v.form.password.$params.maxLength.max }} letters.</div>
            </div>
          </div>
          <button type="submit" class="btn btn-primary btn-block">Create account</button>
          <p class="accept-terms text-muted">
              By clicking “Create account”, you agree to our
              <a place="termsOfService" href="#">terms of service</a> and
              <a place="privacyPolicy" href="#">privacy policy</a>.
          </p>
          <p class="text-center text-muted">Already have an account? <router-link to="login">Sign in</router-link></p>
        </form>
      </div>
    </div>
    <PageFooter/>
  </div>
</template>

<script>
import { required, email, minLength, maxLength, alphaNum } from 'vuelidate/lib/validators'
import registrationService from '@/services/registration'
import Logo from '@/components/Logo.vue'
import PageFooter from '@/components/PageFooter.vue'

export default {
  name: 'RegisterPage',
  data: function () {
    return {
      form: {
        username: '',
        emailAddress: '',
        password: ''
      },
      errorMessage: ''
    }
  },
  components: {
    Logo,
    PageFooter
  },
  validations: {
    form: {
      username: {
        required,
        minLength: minLength(2),
        maxLength: maxLength(50),
        alphaNum
      },
      emailAddress: {
        required,
        email,
        maxLength: maxLength(100)
      },
      password: {
        required,
        minLength: minLength(6),
        maxLength: maxLength(30)
      }
    }
  },
  methods: {
    submitForm () {
      console.log('Submit start======')
      this.$v.$touch()
      if (this.$v.$invalid) {
        return
      }
      console.log('Submit middle======')

      registrationService.register(this.form).then(() => {
        this.$router.push({ name: 'login' })
      }).catch((error) => {
        this.errorMessage = 'Failed to register user. ' + error.message
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.accept-terms {
  margin: 20px 0 40px 0;
}
.login-error{
  color: red;
}
</style>
