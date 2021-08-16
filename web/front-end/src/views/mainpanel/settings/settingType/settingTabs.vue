<template>
<v-container class="" fluid>
  <v-tabs v-model="tabs" style="" fixed-tabs>
          <v-tabs-slider></v-tabs-slider>
          <v-tab href="#mobile-tabs-5-1" style="text-decoration: none;">
            <v-icon>mdi-cog</v-icon> 설정
          </v-tab>
          <v-tab href="#mobile-tabs-5-2" style="text-decoration: none;" @click="getDataToDBcomponent">
            <v-icon>mdi-database-cog</v-icon> DB
          </v-tab>
        </v-tabs>
        <v-divider class="mb-0 mt-0"></v-divider>
    <v-tabs-items v-model="tabs">
      <v-tab-item v-for="i in 2" :key="i" :value="'mobile-tabs-5-' + i">
          <v-scroll-y-transition mode="out-in">
          <v-card class="mt-6 mx-auto" flat>
              <SettingsForm v-if="i == 1" ref="settings" v-bind:selected='selectedList' />
            <v-card-text v-if="i == 2">
              <DBsForm style="" v-bind:selected='selectedList' v-bind:settingPropsList='settingList'/>
            </v-card-text>
          </v-card>
        </v-scroll-y-transition>
      </v-tab-item>
    </v-tabs-items>
    </v-container>
</template>
<script>
import SettingsForm from '@/views/mainpanel/settings/settingType/SettingsForm'
import DBsForm from '@/views/mainpanel/settings/settingType/DBsForm'

export default {
  data () {
    return {
      tabs: null,
      selectedList: this.selected,
      settingList: []
    }
  },
  props: ['selected'],
  components: {
    SettingsForm,
    DBsForm
  },
  computed: {
  },
  methods: {
    getDataToDBcomponent: async function () {
      console.log('Tab Method getDataToDBcomponent executed!!')
      this.settingList = await this.$refs.settings[0].sendDataToDBcomponent()
      console.log(await this.settingList)
      return this.settingList
    }
  }
}
</script>
<style>
.tabStyle {
  height: 10px;
}
</style>
