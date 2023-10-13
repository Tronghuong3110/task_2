import { React, useState, useEffect } from 'react';
import { TableCell, Tooltip, TableSortLabel } from '@mui/material';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import '../../../sass/Probes/ProbeTable/TableHeader.scss';

const TableHeader = (props) => {
    const { valueToOrderBy, orderDirection, handleRequestSort } = props;
    const createSortHandler = (property) => (event) => {
        handleRequestSort(event, property)
    }
    return (
        <TableHead className='Probe_Header'>
            <TableRow>
                <TableCell className='id'>ID</TableCell>
                <TableCell className='probe_name' >PROBE NAME</TableCell>
                <TableCell className='ip_address'>IP ADDRESS</TableCell>
                <TableCell className='location'><Tooltip title="Location">LO</Tooltip></TableCell>
                <TableCell className='area'><Tooltip title="Area">AR</Tooltip></TableCell>
                <TableCell className='vlan'>VLAN</TableCell>
                <TableCell className='probeStatus'>
                    <Tooltip title="Status">STA</Tooltip></TableCell>
                <TableCell className='total_module'>
                    <Tooltip title="Total module">TM</Tooltip>
                    </TableCell>
                <TableCell className='status'>
                    <div className='status-container d-flex'>
                        <TableSortLabel
                            className='status-name running'
                            active={valueToOrderBy === "numberRunningModule"}
                            direction={valueToOrderBy === "numberRunningModule" ? orderDirection : 'asc'}
                            onClick={createSortHandler('numberRunningModule')}
                        ><Tooltip title="Running"><div key="numberRunningModule" className='statusModule' >SR</div></Tooltip></TableSortLabel>
                        <TableSortLabel
                            className='status-name pending'
                            active={valueToOrderBy === "numberPendingModule"}
                            direction={valueToOrderBy === "numberPendingModule" ? orderDirection : 'asc'}
                            onClick={createSortHandler('numberPendingModule')}
                        ><Tooltip title="Pending"><div key="numberPendingModule" className='statusModule'>SP</div></Tooltip></TableSortLabel>
                        <TableSortLabel
                            className='status-name stopped'
                            active={valueToOrderBy === "numberStopedModule"}
                            direction={valueToOrderBy === "numberStopedModule" ? orderDirection : 'asc'}
                            onClick={createSortHandler('numberStopedModule')}
                        ><Tooltip title="Stopped"><div key="numberStopedModule" className='statusModule' >SS</div></Tooltip></TableSortLabel>
                        <TableSortLabel
                            className='status-name failed'
                            active={valueToOrderBy === "numberFailedModule"}
                            direction={valueToOrderBy === "numberFailedModule" ? orderDirection : 'asc'}
                            onClick={createSortHandler('numberFailedModule')}
                        ><Tooltip title="Failed"><div key="numberFailedModule" className='statusModule' >SF</div></Tooltip></TableSortLabel>
                    </div>
                </TableCell>
                <TableCell className='last_online'>LAST ONLINE</TableCell>
                <TableCell className='description'>DESCRIPTION</TableCell>
                <TableCell className='actions'>ACTION</TableCell>
            </TableRow>
        </TableHead>
    )
}
export default TableHeader;