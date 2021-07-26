<template>
  <v-container fluid style="height: 740px;overflow-y:auto;overflow-x: hidden;">
    <v-row class="pt-3">
    <v-textarea
      clearable
      clear-icon="mdi-close-circle"
      label="파일 설명(참조용)"
      rows="3"
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
      <Grid ref="tuiGrid" :data="gridCronProps.data" :columns="gridCronProps.columns" :options="gridCronProps.options" width="500"/>
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
        <p class="itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> 환경(Env)</p>
      <Grid ref="tuiGrid" :data="gridEnvProps.data" :columns="gridEnvProps.columns" :options="gridEnvProps.options" width="600"/>
      </span>
    </v-row>

    <v-spacer style="height: 70px;"></v-spacer>

    <v-row>
      <span class="text-left">
      <p class="text-left itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> Header</p>
        <Grid ref="tuiGrid" :data="gridHeaderProps.data" :columns="gridHeaderProps.columns" :options="gridHeaderProps.options" width="600"/>
      </span>
    </v-row>

    <v-spacer style="height: 70px;"></v-spacer>

    <v-row>
      <span class="text-left">
      <p class="text-left itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> Tail</p>
      <Grid ref="tuiGrid" :data="gridTailProps.data" :columns="gridTailProps.columns" :options="gridTailProps.options" width="600"/>
      </span>
    </v-row>

    <v-spacer style="height: 70px;"></v-spacer>

    <v-row>
      <span class="text-left">
      <p class="text-left itemHeader"><v-icon size="30">mdi-star-four-points</v-icon> 컬럼명(Convert)</p>
      <Grid ref="tuiGrid" :data="gridItemProps.data" :columns="gridItemProps.columns" :options="gridItemProps.options" width="600"/>
      </span>
    </v-row>
      <v-spacer style="height: 70px;"></v-spacer>
  </v-container>
</template>
<script>
import 'tui-grid/dist/tui-grid.css'
import { Grid } from '@toast-ui/vue-grid'
import settingService from '@/services/settings'

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
const headercols = [ // for columnData prop¡~``
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
  }
]

const headercols1 = [ // for columnData prop¡~``
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

const headercols2 = [ // for columnData prop¡~``
  {
    header: 'Item',
    name: 'order_number',
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
    name: 'cm_f02',
    align: 'center',
    editor: 'text'
  }
]

const headercols3 = [ // for columnData prop¡~``
  {
    header: 'Item',
    name: 'order_number',
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
    name: 'cm_f02',
    align: 'center',
    editor: 'text'
  }
]

const headercols4 = [ // query
  {
    header: 'Key',
    name: 'key',
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
    header: 'Query',
    name: 'query',
    align: 'center',
    editor: 'text'
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
    fileDescription: '',
    extractFileType: '',
    gridCronProps: {},
    gridEnvProps: {},
    gridHeadProps: {},
    gridTailProps: {},
    gridItemProps: {}
  }),
  methods: {
    getFileData () {
      settingService.setting(this.selected.id).then(response => {
        this.fileDescription = response.filedefList.file_desc
        this.gridCronProps = { columns: headercols1, options: options1 }
        this.gridCronProps.data = [
          {
            datetype: '월',
            value: response.filedefList.schedule_month
          },
          {
            datetype: '주',
            value: response.filedefList.schedule_week
          },
          {
            datetype: '일',
            value: response.filedefList.schedule_day
          },
          {
            datetype: '시',
            value: response.filedefList.schedule_hour
          },
          {
            datetype: '분',
            value: response.filedefList.schedule_min
          }
        ]
        this.extractFileType = response.filedefList.extract_type
        this.gridEnvProps = { columns: headercols, options: options1, data: response.envList }
        this.gridHeadProps = { columns: headercols2, options: options1 }
        this.gridHeadProps.data = response.hntList.forEach(element => {
          const headerResult = []
          if (element.data_type.startsWith('h')) headerResult.push(element)
          return headerResult
        })
        this.gridTailProps = { columns: headercols3, options: options1 }
        this.gridTailProps.data = response.hntList.forEach(element => {
          const tailResult = []
          if (element.data_type.startsWith('t')) tailResult.push(element)
          return tailResult
        })
        this.gridItemProps = { columns: headercols4, options: options1, data: response.itemList }
      })
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
