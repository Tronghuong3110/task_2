import { Box, ListItemButton, ListItemIcon, ListItemText } from '@mui/material'
import React, { useState } from 'react'
import SidebarItemLink from './SidebarItemLink'
import ExpandMoreOutlinedIcon from '@mui/icons-material/ExpandMoreOutlined';
import KeyboardArrowUpOutlinedIcon from '@mui/icons-material/KeyboardArrowUpOutlined';
const SiderBarItemDrop = ({ item, toggleDrawer }) => {
    const [dropDown,setDropDown] = useState(false)
    const handleDropDown = ()=>{
        setDropDown(!dropDown)
    }
    return (
        <React.Fragment>
            <ListItemButton
                component={Box}
                sx={style.ListItemButton}
                onClick={handleDropDown}
            >
                <ListItemIcon sx={style.listItemIcon}>
                    {item.icon}
                </ListItemIcon>
                <ListItemText>
                    {item.displayItemText}
                </ListItemText>

                {dropDown?(<KeyboardArrowUpOutlinedIcon style={{ marginLeft: "20px" }} />):(<ExpandMoreOutlinedIcon style={{ marginLeft: "20px" }} />)}
            </ListItemButton>
            {dropDown && item.child.map(child => {
                return (
                    <SidebarItemLink item={child} toggleDrawer={toggleDrawer} child={1} />
                )
            })}
        </React.Fragment>
    )
}
/** @type {import("@mui/material").SxProps} */
const style = {
    ListItemButton: {
        padding: "16px 32px"
    },
    listItemIcon: {
        minWidth: "30px",
        marginLeft: "5px"
    }
}
export default SiderBarItemDrop