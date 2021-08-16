<template>
  <v-container fluid>
    <v-row align="center">
      <v-col
        class="d-flex"
        cols="12"
        sm="3"
      >
        <v-select
          :items="items"
          item-text="name"
          item-value="value"
          v-model="selectedItemValue"
          label="DB Type"
          dense
          outlined
        ></v-select>
      </v-col>
      <v-col>
        <v-btn tile color="primary" v-on:click="showResult()">
          <v-icon left size="30">
            mdi-database-search
          </v-icon>
            Test
        </v-btn>
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <v-btn tile color="default" v-on:click="saveData()" >
          <v-icon left size="30">
          mdi-content-save
        </v-icon>
            Save
        </v-btn>
      </v-col>
      </v-row>
      <v-row>
        <span>
  <h5 class="text-left">{{ selectedItemValue.toUpperCase() }} DB</h5>
    <div class="codemirror">
      <codemirror ref="myCm"
                  v-model="formData.queryText"
                  :options="cmOptions"
                  @ready="onCmReady"
                  @focus="onCmFocus"
                  @input="onCmCodeChange"
                  style="text-align: left;">
      </codemirror>
    </div>
        </span>
    </v-row>
  </v-container>
</template>
<script>
import 'codemirror/mode/sql/sql.js'
import 'codemirror/theme/solarized.css'
import 'codemirror/addon/selection/active-line.js'
import 'codemirror/addon/edit/closebrackets.js'
import 'codemirror/mode/clike/clike.js'
import 'codemirror/addon/edit/matchbrackets.js'
import 'codemirror/addon/comment/comment.js'
import 'codemirror/addon/dialog/dialog.js'
import 'codemirror/addon/dialog/dialog.css'
import 'codemirror/addon/search/searchcursor.js'
import 'codemirror/addon/search/search.js'
import 'codemirror/keymap/emacs.js'
import settingService from '@/services/settings'
import FileSaver from 'file-saver'

export default {
  data () {
    return {
      formData: {
        queryText: ''
      },
      content: '',
      selectedItemValue: '',
      cmOptions: {
        tabSize: 4,
        styleActiveLine: true,
        lineNumbers: true,
        line: true,
        mode: 'text/x-sql',
        theme: 'solarized light',
        lineWrapping: true
      },
      items: [
        { name: 'Mes', value: 'MES' },
        { name: 'Report', value: 'REPORT' },
        { name: 'Coms', value: 'COMS' }
      ]
    }
  },
  props: ['selected', 'settingPropsList'],
  computed: {
    codemirror () {
      return this.$refs.myCm.codemirror
    }
  },
  methods: {
    showSQL (val) {
      if (this.formData.queryText === val) {
        return val
      } else {
        return ''
      }
    },
    onCmReady (cm) {
      this.codemirror.setSize('800', '500')
    },
    onCmFocus (cm) {
      console.log('the editor is focus!', this.settingPropsList)
    },
    onCmCodeChange (newCode) {
      this.formData.queryText = newCode
      this.$emit('codeChange', this.formData.queryText)
    },
    saveData () {
      alert('saved~!')
    },
    async showResult () {
      let result = await settingService.selectSQL({ query: this.formData.queryText, database: this.selectedItemValue })

      this.content = await this.settingPropsList.envList.find(env => env.item === 'start').value
      const end = await this.settingPropsList.envList.find(env => env.item === 'end').value
      const between = await this.settingPropsList.envList.find(env => env.item === 'between').value

      await result.returnList.forEach(element => {
        for (const [, value] of Object.entries(element)) {
          this.content += `${value} ` + between
        }
        this.content.slice(-(between.length-1))
        this.content += end + '\r\n'
      })

      let blob = await new Blob([this.content], { type: 'text/plain;charset=utf-8' })
      await FileSaver.saveAs(blob, 'download name .txt')
    }
  }

}
</script>
