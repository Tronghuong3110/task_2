import React from 'react'
import SiderBarItemDrop from './SiderBarItemDrop'
import SidebarItemLink from './SidebarItemLink'

const SidebarItem = ({ item, toggleDrawer }) => {
  return (
    <React.Fragment>
        {
            item.child.length !==0?
            (
                <SiderBarItemDrop item={item} toggleDrawer={toggleDrawer} />
            ):
            (
                <SidebarItemLink item={item} toggleDrawer={toggleDrawer} child={0} />
            )
        }
    </React.Fragment>
  )
}

export default SidebarItem