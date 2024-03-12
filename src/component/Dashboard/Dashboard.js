import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Dashboard/Dashboard.scss';
import ProbeConnection from './Content/ProbeConnection'
import ModulesStatus from './Content/ModulesStatus';
import ProbeHistory from './Content/ProbeHistory';

const Dashboard =() =>{
    return (
        <div className="dashboard d-flex justify-content-between">
            <div className='ProbeAndModuleContainer'>
                <ProbeConnection></ProbeConnection>
                <ModulesStatus></ModulesStatus>
            </div>
            <div className='ProbeHistoryContainer'>
                <ProbeHistory></ProbeHistory>
            </div>
        </div>
    )
}
export default Dashboard