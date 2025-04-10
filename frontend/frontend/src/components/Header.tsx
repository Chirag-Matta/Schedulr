
import React from 'react';
import { Clock } from 'lucide-react';

const Header: React.FC = () => {
  return (
    <header className="bg-gradient-to-r from-timeweaver-blue-600 to-timeweaver-blue-500 text-white p-6">
      <div className="container mx-auto">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Clock size={32} />
            <div>
              <h1 className="text-2xl font-bold">Time Weaver</h1>
              <p className="text-sm opacity-90">Scheduler Dashboard</p>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;
