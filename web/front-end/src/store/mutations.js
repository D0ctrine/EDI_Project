export default {
  updateCategory (state, category) {
    state.categories.forEach(item => {
      if (item.id === category.id) {
        item = category
      }
    })
  },
  deleteCategory (state, category) {
    let idx = 0
    let deleteidx = 0
    state.categories.forEach(item => {
      if (item.id === category.id) deleteidx = idx
      idx++
    })
    state.categories.splice(deleteidx, 1)
  },
  addCategory (state, category) {
    state.categories.push(category)
  },
  addTeam (state, team) {
    state.teams.push(team)
  },
  addBoard (state, board) {
    state.boards.push(board)
  }
}
