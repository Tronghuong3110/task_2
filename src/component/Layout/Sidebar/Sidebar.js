import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import "@fontsource/montserrat";
import {
    faChartLine,
    faClockRotateLeft,
    faCube,
    faDisplay,
    faRecycle, faServer, faHardDrive, faWarehouse, faDatabase
} from '@fortawesome/free-solid-svg-icons'

import { Box } from '@mui/material';
import SidebarItem from './SidebarItem';
const Sidebar = (props) => {
    const { toggleDrawer } = props
    const sidebarItem = [
        {
            displayItemText: "Probes",
            linkTo: '/probe',
            icon: <FontAwesomeIcon icon={faDisplay} style={{ color: "#ffffff", fontSize: "1em" }} />,
            child: [
                {
                    displayItemText: "Probe Manage",
                    linkTo: '/probe',
                    icon: <FontAwesomeIcon icon={faDisplay} style={{ color: "#ffffff", fontSize: "1em" }} />,
                },
                {
                    displayItemText: "History",
                    linkTo: '/module_history/0',
                    icon: <FontAwesomeIcon icon={faClockRotateLeft} style={{ color: "#ffffff", fontSize: "1em" }} />
                },
                {
                    displayItemText: "Recycle bin",
                    linkTo: '/bin',
                    icon: <FontAwesomeIcon icon={faRecycle} style={{ color: "#ffffff", fontSize: "1em" }} />
                }
            ]
        },
        {
            displayItemText: "Modules",
            linkTo: '/modules',
            icon: <FontAwesomeIcon icon={faCube} style={{ color: "#ffffff", fontSize: "1em" }} />,
            child: []
        },
        {
            displayItemText: "DB Server",
            linkTo: '/dbserver',
            icon: <FontAwesomeIcon icon={faServer} style={{ color: "#ffffff", fontSize: "1em" }} />,
            child: [
                {
                    displayItemText: "DB Server Manage",
                    linkTo: '/dbserver',
                    icon: <FontAwesomeIcon icon={faServer} style={{ color: "#ffffff", fontSize: "1em" }} />,
                },
                {
                    displayItemText: "Database",
                    linkTo: '/db',
                    icon: <FontAwesomeIcon icon={faDatabase} style={{ color: "#ffffff", fontSize: "1em" }} />
                },
                {
                    displayItemText: "Capture",
                    linkTo: '/capture',
                    icon: <FontAwesomeIcon icon={faWarehouse} style={{ color: "#ffffff", fontSize: "1em" }} />
                }
            ]
        },
        {
            displayItemText: "NAS",
            linkTo: '/nas',
            icon: <FontAwesomeIcon icon={faHardDrive} style={{ color: "#ffffff", fontSize: "1em" }} />,
            child: []
        }

    ]
    return (
        <Box sx={style.sideBar} >
            {
                sidebarItem.map(item => {
                    return (
                        <SidebarItem item={item} toggleDrawer={toggleDrawer} />
                    )
                })
            }
        </Box>
    )
}
/** @type {import("@mui/material").SxProps} */
const style = {
    sideBar: {
        height: "100%",
        color: "white",
        backgroundImage: "linear-gradient(#E14ECA,#9747FF)",

    }
}
export default Sidebar;