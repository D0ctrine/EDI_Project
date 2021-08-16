<template>
<v-app>
  <p style="">
  <v-btn color="blue" class="mr-16 white--text" v-on:click="saveData()" style="float: right">
      Save<v-icon right dark> mdi-content-save-all</v-icon>
    </v-btn></p>
  <v-container fluid style="height: 740px;overflow-y:auto;overflow-x: hidden;">
    <v-row class="pt-3">
    <v-textarea
      clearable
      clear-icon="mdi-close-circle"
      label="파일 설명(참조용)"
      rows="2"
      style="font-size: 18px;"
      placeholder="파일 내용을 간략히 작성하시오."
      v-model="fileDescription"
      class="pt-0"
    ></v-textarea>
    </v-row>

    <v-spacer style="height: 50px;"></v-spacer>

    <v-row>
      <span class="text-left">
        <p class="itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> 주기(Crontab)</p>
      <VueCronEditorBuefy v-model="cronExpression"/>
    {{cronExpression}}
      </span>
    </v-row>
    <v-spacer style="height: 70px;"></v-spacer>

      <v-row>
        <span class="text-left">
          <p class="itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> 파일 추출 방식</p>
    <v-radio-group v-model="extractFileType" row dense>
      <v-radio label="Text" value="text"></v-radio>
      <v-radio label="XML" value="xml"></v-radio>
      <v-radio label="HTML" value="html"></v-radio>
    </v-radio-group>
        </span>
    </v-row>

    <v-spacer style="height: 50px;"></v-spacer>

    <v-row>
      <span class="text-left">
        <p class="itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> 환경(Env)
          <v-btn class="mx-2" v-on:click="insertRow('envGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
        </p>
      <Grid ref="envGrid" :data="gridEnvProps.data" :columns="gridEnvProps.columns" :options="gridEnvProps.options" width="600"/>
      </span>
    </v-row>

    <v-spacer style="height: 70px;"></v-spacer>

    <v-row>
      <span class="text-left">
      <p class="text-left itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> Header
          <v-btn class="mx-2" v-on:click="insertRow('headGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
      </p>
        <Grid ref="headGrid" :data="gridHeadProps.data" :columns="gridHeadProps.columns" :options="gridHeadProps.options" width="600"/>
      </span>
    </v-row>

    <v-spacer style="height: 70px;"></v-spacer>

    <v-row>
      <span class="text-left">
      <p class="text-left itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> Tail
          <v-btn class="mx-2" v-on:click="insertRow('tailGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
      </p>
      <Grid ref="tailGrid" :data="gridTailProps.data" :columns="gridTailProps.columns" :options="gridTailProps.options" width="600"/>
      </span>
    </v-row>

    <v-spacer style="height: 70px;"></v-spacer>

    <v-row>
      <span class="text-left">
      <p class="text-left itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> 컬럼명(Convert)
          <v-btn class="mx-2" v-on:click="insertRow('itemGrid')" style="float: right;" fab small dark color="indigo">
            <v-icon dark>
              mdi-plus
            </v-icon>
          </v-btn>
      </p>
      <Grid ref="itemGrid" :data="gridItemProps.data" :columns="gridItemProps.columns" :options="gridItemProps.options" width="600"/>
      </span>
    </v-row>
      <v-spacer style="height: 70px;"></v-spacer>
  </v-container>
</v-app>
</template>
<script>
import 'tui-grid/dist/tui-grid.css'
import { Grid } from '@toast-ui/vue-grid'
import settingService from '@/services/settings'
import VueCronEditorBuefy from 'vue-cron-editor-buefy'

class CustomDownBtnRenderer {
  constructor (props) {
    var el = document.createElement('button')
    el.innerHTML = '<span class="mdi mdi-close mdi-24px"></span>'
    el.style.width = '100%'
    el.style.backgroundColor = 'white'
    el.addEventListener('click', function () {
      const { grid, rowKey, columnInfo } = props
      console.log('columnInfo')
      console.log(columnInfo)
      var deleteRow = grid.getRow(rowKey)
      console.log('deleteRow')
      console.log(deleteRow)
      if (deleteRow.gridName === 'envGrid')settingService.deleteEnv(deleteRow)
      else if (deleteRow.gridName === 'headGrid' || deleteRow.gridName === 'tailGrid')settingService.deleteHeadNTail(deleteRow)
      else if (deleteRow.gridName === 'itemGrid')settingService.deleteQuery(deleteRow)
      grid.removeRow(rowKey)
    })
    this.el = el
    this.render(props)
  }

  getElement () {
    return this.el
  }

  render (props) {
    this.el.value = String(props.value)
  }
}

const options1 = {
  scrollX: false,
  scrollY: false,
  height: 'auto',
  rowHeight: 30,
  rowHeaders: ['rowNum'],
  header: {
    height: 30
  }
}
const headercols = [ // Env
  {
    header: 'Item',
    name: 'item',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Note',
    name: 'note',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

const headercols2 = [ // Head
  {
    header: 'SEQ',
    name: 'id',
    hidden: true
  },
  {
    header: 'Type',
    name: 'data_type',
    align: 'center',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: 'Text', value: 'h_text' },
          { text: 'SQL', value: 'h_sql' }
        ]
      }
    }
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Note',
    name: 'cm_f02',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

const headercols3 = [ // Tail
  {
    header: 'SEQ',
    name: 'id',
    hidden: true
  },
  {
    header: 'Type',
    name: 'data_type',
    align: 'center',
    formatter: 'listItemText',
    editor: {
      type: 'select',
      options: {
        listItems: [
          { text: 'Text', value: 't_text' },
          { text: 'SQL', value: 't_sql' }
        ]
      }
    }
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Note',
    name: 'note',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

const headercols4 = [ // query
  {
    header: 'Alias',
    name: 'key',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Query',
    name: 'query',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Type',
    name: 'type',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Del',
    name: 'del',
    align: 'center',
    width: '60',
    renderer: { type: CustomDownBtnRenderer }
  }
]

export default {
  name: 'myGrid',
  props: ['selected'],
  components: {
    Grid, VueCronEditorBuefy
  },
  created () {
    this.getFileData()
  },
  data: () => ({
    cronExpression: '',
    fileDescription: 'No Explanations',
    fileDescriptionID: 0,
    extractFileType: 'text',
    gridEnvProps: { columns: headercols, options: options1 },
    gridHeadProps: { columns: headercols2, options: options1 },
    gridTailProps: { columns: headercols3, options: options1 },
    gridItemProps: { columns: headercols4, options: options1 }
  }),
  methods: {
    getFileData () {
      console.log(this.selected)
      settingService.setting(this.selected.id)
        .then(async response => {
          if (response.filedefList) {
            this.fileDescriptionID = await response.filedefList.id
            this.fileDescription = await response.filedefList.file_desc
            this.cronExpression = await response.filedefList.cron_data
            this.extractFileType = await response.filedefList.extract_type
          } else {
            let fileDef = await { data: this.cronExpression, fileDesc: this.fileDescription, extractType: this.extractFileType }
            const fileSetting = await { env: [], fileDef: fileDef, headNtail: [], itemGrp: [], cg_id: this.selected.id }
            await settingService.create(fileSetting)
          }

          let headerResult = []
          this.gridHeadProps.data = await response.hntList.forEach(element => {
            if (element.data_type.startsWith('h')) {
              element.gridName = 'headGrid'
              headerResult.push(element)
            }
          })
          this.gridHeadProps.data = headerResult

          let tailResult = []
          await response.hntList.forEach(element => {
            if (element.data_type.startsWith('t')) {
              element.gridName = 'tailGrid'
              tailResult.push(element)
            }
          })
          this.gridTailProps.data = tailResult

          await response.envList.forEach((element) => { element.gridName = 'envGrid' })
          await response.itemList.forEach((element) => { element.gridName = 'itemGrid' })

          console.log('gridProps Head N Tail')
          console.log(this.gridHeadProps.data)
          console.log(this.gridTailProps.data)
          await this.$refs.envGrid.invoke('resetData', response.envList)
          await this.$refs.itemGrid.invoke('resetData', response.itemList)
          await this.$refs.tailGrid.invoke('resetData', this.gridTailProps.data)
          await this.$refs.headGrid.invoke('resetData', this.gridHeadProps.data)
        })
    },
    insertRow: function (event) {
      var rowData = { item: 'New Item', value: 'New Value', note: 'New Note' }
      let grid = ''
      console.log('event')
      console.log(event)
      if (event === 'envGrid') {
        rowData.gridName = 'envGrid'
        grid = this.$refs.envGrid
      } else if (event === 'headGrid') {
        rowData.gridName = 'headGrid'
        rowData.dataType = 'h_text'
        grid = this.$refs.headGrid
      } else if (event === 'tailGrid') {
        rowData.gridName = 'tailGrid'
        rowData.dataType = 't_text'
        grid = this.$refs.tailGrid
      } else if (event === 'itemGrid') {
        rowData.gridName = 'itemGrid'
        grid = this.$refs.itemGrid
      }

      const cnt = grid.invoke('getRowCount')
      console.log(rowData)
      grid.invoke('appendRow', rowData, { at: cnt, focus: true })
    },
    saveData: async function () {
      if (confirm('저장하시겠습니까?')) {
        let env = await this.$refs.envGrid.invoke('getModifiedRows').createdRows
        let head = await this.$refs.headGrid.invoke('getModifiedRows').createdRows
        let tail = await this.$refs.tailGrid.invoke('getModifiedRows').createdRows
        let headNtail = await [ ...head, ...tail ]
        let itemGrp = await this.$refs.itemGrid.invoke('getModifiedRows').createdRows
        console.log('headNtail')
        console.log(headNtail)
        const fileSetting = await {
          env: env,
          headNtail: headNtail,
          itemGrp: itemGrp,
          cg_id: this.selected.id + ''
        }

        await settingService.create(fileSetting)

        let fileDef = await { id: this.fileDescriptionID, cronData: this.cronExpression, fileDesc: this.fileDescription, extractType: this.extractFileType }
        env = await this.$refs.envGrid.invoke('getModifiedRows').updatedRows
        head = await this.$refs.headGrid.invoke('getModifiedRows').updatedRows
        tail = await this.$refs.tailGrid.invoke('getModifiedRows').updatedRows
        headNtail = await [ ...head, ...tail ]
        itemGrp = await this.$refs.itemGrid.invoke('getModifiedRows').updatedRows

        const updateSetting = await {
          env: env,
          headNtail: headNtail,
          fileDef: fileDef,
          itemGrp: itemGrp,
          cg_id: this.selected.id
        }
        await settingService.update(updateSetting)

        await this.getFileData()
      }
    },
    sendDataToDBcomponent: function () {
      const propsList = {
        envList: this.$refs.envGrid.invoke('getData'),
        headerList: this.$refs.headGrid.invoke('getData'),
        tailList: this.$refs.tailGrid.invoke('getData'),
        itemList: this.$refs.itemGrid.invoke('getData'),
        fileType: this.extractFileType
      }
      console.log('Tab Method sendDataToDBcomponent executed!!')
      return propsList
    }
  }
}
</script>

<style>
  .v-label {
    font-size: 13px;
  }
  .itemHeader {
    font-weight: bold;
    font-size: 14px;
  }
</style>
