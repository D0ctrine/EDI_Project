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
  <h5 class="text-left" style="float: left;">{{ selectedItemValue }} DB</h5>
  <h6 style="float: right;color: red;">{{ modifiedAlarm }}</h6>
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
import convert from 'xml-js'

export default {
  data () {
    return {
      formData: {
        queryText: '',
        orginalQueryText: ''
      },
      content: '',
      selectedItemValue: '',
      modifiedAlarm: '',
      modifiedTime: '',
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
  created () {
    settingService.getSQL(this.selected.id).then((returnList) => {
      this.formData.queryText = returnList.MainQuery.query
      this.formData.orginalQueryText = returnList.MainQuery.query
      this.selectedItemValue = returnList.MainQuery.db_type
    }).catch(error => {
      this.errorMessage = error.message
      console.log(this.errorMessage)
    })
  },
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
      console.log('the editor is focus!')
    },
    onCmCodeChange (newCode) {
      if (this.formData.orginalQueryText !== newCode) {
        if (this.modifiedTime === '') {
          let today = new Date()
          this.modifiedTime = today.toLocaleString()
          this.modifiedAlarm = this.modifiedTime + '  변경 시작됨(저장 필요!)'
        }
      } else {
        this.modifiedAlarm = ''
        this.modifiedTime = ''
      }
      this.formData.queryText = newCode
      this.$emit('codeChange', this.formData.queryText)
    },
    refreshData () {
      settingService.getSQL(this.selected.id).then((returnList) => {
        this.formData.queryText = returnList.MainQuery.query
        this.formData.orginalQueryText = returnList.MainQuery.query
        this.selectedItemValue = returnList.MainQuery.db_type
      }).catch(error => {
        this.errorMessage = error.message
        console.log(this.errorMessage)
      })
    },
    async saveData () {
      await settingService.saveSQL({ query: this.formData.queryText, type: 'Main', settingId: this.selected.id }).then(() => {
        alert('Saved!')
        this.orginalQueryText = this.formData.queryText
        this.modifiedTime = ''
      }).catch(error => { alert(error.message) })
    },
    async showResult () {
      let result = await settingService.selectSQL({ query: this.formData.queryText, database: this.selectedItemValue })
      const header = await this.settingPropsList.headerList
      const tail = await this.settingPropsList.tailList
      const start = await this.settingPropsList.envList.find(env => env.item === 'start').value
      const end = await this.settingPropsList.envList.find(env => env.item === 'end').value
      const between = await this.settingPropsList.envList.find(env => env.item === 'between').value
      this.content = ''

      for (const element of header) {
        console.log(element)
        if (element.data_type.toString() === 'h_sql') {
          this.content += await settingService.selectSQL({ query: element.value.toString(), database: 'REPORT' }).then((sqlList) => sqlList.returnList[0].VALUE).catch(error => { console.log(error.message) })
        } else {
          this.content += await element.value
        }
        // tail 1,2,3,,,마다 줄 바꿈
        this.content += await '\r\n'
      }

      if (this.settingPropsList.dataType === 'xml') {
        console.log('Its XML !=======')
        let parent = await this.settingPropsList.envList.find(env => env.item === 'parent').value
        let flatArrayJson = {}
        for (const element of result.returnList) {
          flatArrayJson[parent] = { _attributes: element }
          this.content += await convert.json2xml(flatArrayJson, { compact: true, ignoreComment: true, spaces: 4 })
          this.content += '\r\n'
        }
      } else if (this.settingPropsList.dataType === 'text') {
        for (const element of result.returnList) {
          this.content += await start
          for (const [, value] of Object.entries(element)) {
            this.content += await `${value}` + between
          }
          this.content.slice(-(between.length - 1))
          this.content += end.value + '\r\n'
        }
      } else {

      }

      await tail.forEach(element => {
        if (element.data_type.toString() === 't_sql') {
          settingService.selectSQL({ query: element.value.toString(), database: 'REPORT' }).then((sqlList) => {
            console.log('sqlList ======')
            console.log(sqlList)
            sqlList.forEach(element => {
              for (const [, value] of Object.entries(element)) {
                this.content += `${value} ` + between
              }
            })
          }).catch(error => { console.log(error.message) })
        } else {
          this.content += element.value
        }
        // header 1,2,3,,,마다 줄 바꿈
        this.content += '\r\n'
      })
      let blob = await new Blob([this.content], { type: 'text/plain;charset=' + this.settingPropsList.charSet })
      await FileSaver.saveAs(blob, this.settingPropsList.fileName + '.' + this.settingPropsList.fileType)
    }
  }

}
</script>
