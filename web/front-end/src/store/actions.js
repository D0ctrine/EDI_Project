
export const addCategory = ({ commit }, category) => {
  commit('addCategory', category)
}

export const updateCategory = ({ commit }, category) => {
  commit('updateCategory', category)
}

export const deleteCategory = ({ commit }, category) => {
  commit('deleteCategory', category)
}

export const addTeam = ({ commit }, team) => {
  commit('addTeam', team)
}

export const addBoard = ({ commit }, board) => {
  commit('addBoard', board)
}
