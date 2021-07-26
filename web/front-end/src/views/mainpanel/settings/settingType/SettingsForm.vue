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
      style="font-size: 12px;"
      placeholder="파일 내용을 간략히 작성하시오."
      v-model="fileDescription"
      class="pt-0"
    ></v-textarea>
    </v-row>

    <v-spacer style="height: 50px;"></v-spacer>

    <v-row>
      <span class="text-left">
        <p class="itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> 주기(Crontab)</p>
      <Grid ref="cronGrid" :data="gridCronProps.data" :columns="gridCronProps.columns" :options="gridCronProps.options" width="500"/>
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

class CustomDownBtnRenderer {
  constructor (props) {
    var el = document.createElement('button')
    el.innerHTML = '<span class="mdi mdi-close mdi-24px"></span>'
    el.style.width = '100%'
    el.style.backgroundColor = 'white'
    el.addEventListener('click', function () {
      const { grid, rowKey, columnInfo } = props
      console.log('grid')
      console.log(grid)
      console.log('columnInfo')
      console.log(columnInfo)
      var deleteRow = grid.getRow(rowKey)
      console.log('deleteRow')
      console.log(deleteRow)
      if (deleteRow.gridName === 'envGrid')settingService.deleteEnv(deleteRow)
      else if (deleteRow.gridName === 'headGrid')settingService.deleteHeadNTail(deleteRow)
      else if (deleteRow.gridName === 'tailGrid')settingService.deleteHeadNTail(deleteRow)
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
const headercols = [ // Cron
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

const headercols1 = [ // Env
  {
    header: 'DateType',
    name: 'datetype',
    width: '80',
    align: 'center',
    editor: 'text'
  },
  {
    header: 'Value',
    name: 'value',
    align: 'center',
    editor: 'text'
  }
]

const headercols2 = [ // Head
  {
    header: 'Type',
    name: 'dataType',
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
    header: 'Type',
    name: 'dataType',
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
    Grid
  },
  created () {
    this.getFileData()
  },
  data: () => ({
    fileDescription: 'No Explanations',
    extractFileType: 'text',
    gridCronProps: {
      columns: headercols1,
      options: options1,
      data: [{
        datetype: 'Month',
        value: ''
      },
      {
        datetype: 'Week',
        value: ''
      },
      {
        datetype: 'Day',
        value: ''
      },
      {
        datetype: 'Hour',
        value: ''
      },
      {
        datetype: 'Min',
        value: ''
      }] },
    gridEnvProps: { columns: headercols, options: options1 },
    gridHeadProps: { columns: headercols2, options: options1 },
    gridTailProps: { columns: headercols3, options: options1 },
    gridItemProps: { columns: headercols4, options: options1 }
  }),
  methods: {
    getFileData () {
      console.log(this.selected)
      settingService.setting(this.selected.id).then(response => {
        if (response.filedefList.lenth) {
          this.fileDescription = response.filedefList[0].file_desc
          this.gridCronProps.data = [
            {
              datetype: 'Month',
              value: response.filedefList[0].schedule_month
            },
            {
              datetype: 'Week',
              value: response.filedefList[0].schedule_week
            },
            {
              datetype: 'Day',
              value: response.filedefList[0].schedule_day
            },
            {
              datetype: 'Hour',
              value: response.filedefList[0].schedule_hour
            },
            {
              datetype: 'Min',
              value: response.filedefList[0].schedule_min
            }
          ]
          this.extractFileType = response.filedefList[0].extract_type
        } else {
          let fileDef = this.gridCronProps.data
          fileDef[0].fileDesc = this.fileDescription
          fileDef[0].extractType = this.extractFileType
        }

        this.gridHeadProps.data = response.hntList.forEach(element => {
          let headerResult = []
          if (element.data_type.startsWith('h')) headerResult.push(element)
          return headerResult
        })

        this.gridTailProps.data = response.hntList.forEach(element => {
          let tailResult = []
          if (element.data_type.startsWith('t')) tailResult.push(element)
          return tailResult
        })

        this.$refs.cronGrid.invoke('resetData', this.gridCronProps.data)
        this.$refs.envGrid.invoke('resetData', response.envList)
        this.$refs.headGrid.invoke('resetData', this.gridHeadProps.data)
        this.$refs.tailGrid.invoke('resetData', this.gridTailProps.data)
        this.$refs.itemGrid.invoke('resetData', response.itemList)
      })
    },
    insertRow: function (event) {
      var rowData = { item: 'New Item', value: 'New Value', note: 'New Note' }
      let grid = ''
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
      grid.invoke('appendRow', rowData, { at: cnt, focus: true })
    },
    saveData: async function () {
      if (confirm('저장하시겠습니까?')) {
        let env = await this.$refs.envGrid.invoke('getModifiedRows').createdRows
        let head = await this.$refs.headGrid.invoke('getModifiedRows').createdRows
        let tail = await this.$refs.tailGrid.invoke('getModifiedRows').createdRows
        let headNtail = await [ ...head, ...tail ]

        let itemGrp = await this.$refs.itemGrid.invoke('getModifiedRows').createdRows

        const fileSetting = await {
          env: env,
          headNtail: headNtail,
          item: itemGrp,
          cg_id: this.selected.id
        }
        console.log('fileSetting List')
        console.log(fileSetting)

        await settingService.create(fileSetting)

        let fileDef = await this.gridCronProps.data
        fileDef[0].fileDesc = this.fileDescription
        fileDef[0].extractType = this.extractFileType
        env = await this.$refs.envGrid.invoke('getModifiedRows').updatedRows
        head = await this.$refs.headGrid.invoke('getModifiedRows').updatedRows
        tail = await this.$refs.tailGrid.invoke('getModifiedRows').updatedRows
        headNtail = await [ ...head, ...tail ]
        itemGrp = await this.$refs.itemGrid.invoke('getModifiedRows').updatedRows

        const updateSetting = await {
          env: env,
          headNtail: headNtail,
          fileDef: fileDef,
          item: itemGrp,
          cg_id: this.selected.id
        }
        console.log('updateSetting List')
        console.log(updateSetting)
        await settingService.update(updateSetting)

        await this.getFileData()
      }
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
