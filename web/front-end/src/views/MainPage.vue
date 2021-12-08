<template>
<v-app>
  <v-navigation-drawer app v-model="drawer" :width="180" style="background-color: RGB(241, 196, 15);">
    <v-list-item>
        <v-list-item-content>
          <v-list-item-title>
            <v-btn to="/" class="ml-15 no-background-hover" style="font-size: 30px;text-decoration: none;" icon><font-awesome-icon icon="file-alt" style="color: red;" size="lg"/>&nbsp;EDI</v-btn>
          </v-list-item-title>
          <v-divider></v-divider>
        </v-list-item-content>
      </v-list-item>
      <v-list dense nav>
          <v-list-item v-for="(item, i) in items" :key="i" :to="{ name: item.link }" link>
          <v-list-item-icon>
            <v-icon size="32" class="ml-1">{{item.icon}}</v-icon>
          </v-list-item-icon>
          <v-list-item-content>
            <v-list-item-title style="font-size: 15px;font: bolder;font-weight: bolder;">{{item.text}}</v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
  </v-navigation-drawer>

  <v-app-bar app class="grey lighten-1">
      <v-app-bar-nav-icon @click.stop="drawer = !drawer"></v-app-bar-nav-icon>
      <v-toolbar-title>File Management System</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn icon>
        <v-icon>mdi-magnify</v-icon>
      </v-btn>
      <v-btn icon>
        <v-icon>mdi-heart</v-icon>
      </v-btn>
      <v-btn icon>
        <v-icon>mdi-dots-vertical</v-icon>
      </v-btn>
  </v-app-bar>

  <!-- Sizes your content based upon application components -->
  <v-main>
      <!-- If using vue-router -->
      <router-view></router-view>
  </v-main>

  <v-footer app>
    <!-- -->
  </v-footer>
</v-app>
</template>

<script>
export default {
  name: 'MainPage',
  data: function () {
    return {
      items: [
        {
          icon: 'mdi-calendar-clock',
          text: '발송 현황',
          link: 'report'
        },
        {
          icon: 'mdi-file-cog',
          text: '발송 설정',
          link: 'settings'
        }
      ],
      drawer: null
    }
  }
}
</script>

<style>
.itemtitle {
  font-size: 10px;
}
.no-background-hover::before {
  background-color: transparent !important;
}
</style>
