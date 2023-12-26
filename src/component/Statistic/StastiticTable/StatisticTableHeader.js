import { React, useState, useEffect } from 'react';
import { TableCell, Tooltip, TableSortLabel } from '@mui/material';
import '../../../sass/Statistic/StatisticTable/StatisticHeader.scss'
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';

const StatisticTableHeader = (props) => {
    const { valueToOrderBy, orderDirection, handleRequestSort, selectOrRemoveALL } = props;
    const createSortHandler = (property) => (event) => {
        handleRequestSort(event, property)
    }
    return (
        <TableHead className='StatisticTableHeader'>
            <TableRow>
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
                    ><Tooltip title="Error around time range"><div key="errorPerWeek">Error</div></Tooltip></TableSortLabel></TableCell>
                <TableCell className='note'>Note</TableCell>
            </TableRow>
        </TableHead>
    )
}
export default StatisticTableHeader;