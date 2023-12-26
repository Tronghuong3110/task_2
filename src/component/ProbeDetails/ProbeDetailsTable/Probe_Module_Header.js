import { React, useState, useEffect } from 'react';
import { TableCell, Tooltip, TableSortLabel } from '@mui/material';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import '../../../sass/ProbeDetails/ProbeDetailsTable/Probe_Module_Header.scss';

const Probe_Module_Header = (props) => {
    const { valueToOrderBy, orderDirection, handleRequestSort,selectOrRemoveALL } = props;
    const createSortHandler = (property) => (event) => {
        handleRequestSort(event, property)
    }
    const tickOrUntickALL = ()=>{
        let checkedValue = document.getElementById("main-tick").checked
        selectOrRemoveALL(checkedValue)
        console.log(checkedValue)
    }
    return (
        <TableHead className='Probe_Module_Header'>
            <TableRow>
                <TableCell className='checkbox'>
                    <input id="main-tick" type='checkbox' onClick={()=>{
                        tickOrUntickALL()
                    }}></input>
                </TableCell>
                <TableCell className='id'>Id</TableCell>
                <TableCell className='module_name' >Module name</TableCell>
                <TableCell className='caption'>Caption</TableCell>
                <TableCell className='argument'>Argument</TableCell>
                <TableCell className='errorPerWeek'>
                    <TableSortLabel
                        className='status-name'
                        active={valueToOrderBy === "errorPerWeek"}
                        direction={valueToOrderBy === "errorPerWeek" ? orderDirection : 'asc'}
                        onClick={createSortHandler('errorPerWeek')}
                    ><Tooltip title="Error Per Week"><div key="errorPerWeek">EPW</div></Tooltip></TableSortLabel></TableCell>
                <TableCell className='status'>Status</TableCell>
                <TableCell className='note'>Note</TableCell>
                <TableCell className='actions'>Action</TableCell>
                <TableCell className='processStatus'></TableCell>
            </TableRow>
        </TableHead>
    )
}
export default Probe_Module_Header;