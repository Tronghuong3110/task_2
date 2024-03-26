import { ListItemButton, ListItemIcon, ListItemText } from '@mui/material'
import React from 'react'
import { Link } from 'react-router-dom'
const SidebarItemLink = ({ item, toggleDrawer,child }) => {

  const setStyleForChildLink=(key)=>{
    console.log(key)
    if(key===1){
      return{
        marginLeft:"20px"
      }
    }
    else return {}
  }
  return (
    <React.Fragment>
      <ListItemButton
        component={Link}
        to={item.linkTo}
        sx={style.ListItemButton}
        onClick={toggleDrawer(false)}
        style={child === 1 ? { background: "rgba(0, 0, 0, 0.1)" } : {}}
      >
        <ListItemIcon sx={style.listItemIcon} style={setStyleForChildLink(child)}>
          {item.icon}
        </ListItemIcon>
        <ListItemText >
          {item.displayItemText}
        </ListItemText>
      </ListItemButton>
    </React.Fragment>
  )
}
/** @type {import("@mui/material").SxProps} */
const style = {
  ListItemButton: {
    padding: "16px 32px",
  },
  listItemIcon: {
    minWidth: "30px",
    marginLeft: "5px"
  }
}

export default SidebarItemLink