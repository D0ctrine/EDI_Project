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
            <label for="username">Username or email address</label>
            <div class="input-group form-group">
              <div class="input-group-prepend">
                <span class="input-group-text"><font-awesome-icon icon="user" size="lg"/></span>
              </div>
              <input type="text" class="form-control" id="username" v-model="form.username">
            </div>
            <div class="field-error" v-if="$v.form.username.$dirty">
                <div class="login-error" v-if="!$v.form.username.required">Username or email address is required</div>
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
            </div>
          </div>
          <button type="submit" class="btn btn-success btn-block">Sign in</button>
          <div class="links">
            <p class="sign-up text-muted">Don't have an account yet? <router-link to="register" class="link-sign-up">Sign up here</router-link></p>
            <img class="logo" src="/images/logo.gif">
          </div>
        </form>
      </div>
    </div>
    <PageFooter/>
  </div>
</template>

<script>
import { required } from 'vuelidate/lib/validators'
import authenticationService from '@/services/authentication'
import Logo from '@/components/Logo.vue'
import PageFooter from '@/components/PageFooter.vue'

export default {
  name: 'LoginPage',
  data: function () {
    return {
      form: {
        username: '',
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
        required
      },
      password: {
        required
      }
    }
  },
  methods: {
    submitForm () {
      this.$v.$touch()
      if (this.$v.$invalid) {
        return
      }

      authenticationService.authenticate(this.form).then(() => {
        this.$router.push({ name: 'default' })
      }).catch((error) => {
        this.errorMessage = error.message
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.links {
  margin: 30px 0 50px 0;
  text-align: center;
}
.login-error{
  color: red;
}
</style>
