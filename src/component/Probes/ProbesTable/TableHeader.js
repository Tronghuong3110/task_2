import { React } from 'react';
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
                <TableCell className='id' width="4%">ID</TableCell>
                <TableCell className='probe_name' width="14%" >
                    <div className='probe_name-container'>
                        <TableSortLabel
                            active={valueToOrderBy === "name"}
                            direction={valueToOrderBy === "name" ? orderDirection : 'asc'}
                            onClick={createSortHandler('name')}
                        >
                            <div key="name" >PROBE NAME</div>
                        </TableSortLabel>
                    </div>
                </TableCell>
                <TableCell className='ip_address' width="10%" >IP ADDRESS</TableCell>
                <TableCell className='location' width="5%">
                    <Tooltip title="Location">
                        <span>LO</span>
                    </Tooltip>
                </TableCell>
                <TableCell className='area' width="5%">
                    <Tooltip title="Area">
                        <span>AR</span>
                    </Tooltip>
                </TableCell>
                <TableCell className='vlan' width="5%">VLAN</TableCell>
                <TableCell className='probeStatus' width="5%">
                    <Tooltip title="Status">
                        <span>STA</span>
                    </Tooltip>
                </TableCell>
                <TableCell className='total_module' width="5%">
                    <Tooltip title="Total module">
                        <span>TM</span>
                    </Tooltip>
                </TableCell>
                <TableCell className='status' width='13%'>
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
                <TableCell className='interface' width="8%">INTERFACE</TableCell>
                <TableCell className='description' width="18%">DESCRIPTION</TableCell>
                <TableCell className='actions' width="13%">ACTION</TableCell>
            </TableRow>
        </TableHead>
    )
}
export default TableHeader;